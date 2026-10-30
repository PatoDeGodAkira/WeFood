#!/usr/bin/env python3
"""WeFood local API - Python standard library + SQLite; no external dependencies."""
import json, sqlite3, hashlib, secrets, os, re, mimetypes
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
from datetime import datetime, timedelta, timezone
from pathlib import Path

BASE=Path(__file__).resolve().parent
DB=Path(os.environ.get("WEFOOD_DB", BASE/"wefood.db"))
WEB=BASE/"web"
PORT=int(os.environ.get("PORT","8080"))

SCHEMA="""
PRAGMA foreign_keys=ON;
CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, phone TEXT, password_hash TEXT NOT NULL, role TEXT NOT NULL CHECK(role IN ('CLIENT','RESTAURANT','COURIER','ADMIN')), active INTEGER NOT NULL DEFAULT 1, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS restaurants(id INTEGER PRIMARY KEY, owner_id INTEGER REFERENCES users(id), name TEXT NOT NULL, description TEXT DEFAULT '', phone TEXT DEFAULT '', city TEXT DEFAULT '', address TEXT DEFAULT '', fee REAL NOT NULL DEFAULT 0, min_order REAL NOT NULL DEFAULT 0, prep_minutes INTEGER NOT NULL DEFAULT 30, is_open INTEGER NOT NULL DEFAULT 1, active INTEGER NOT NULL DEFAULT 1, approved INTEGER NOT NULL DEFAULT 1, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY, restaurant_id INTEGER NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE, name TEXT NOT NULL, description TEXT DEFAULT '', category TEXT DEFAULT 'Outros', price REAL NOT NULL CHECK(price>=0), available INTEGER NOT NULL DEFAULT 1, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS couriers(id INTEGER PRIMARY KEY, user_id INTEGER UNIQUE REFERENCES users(id), name TEXT NOT NULL, phone TEXT DEFAULT '', vehicle TEXT DEFAULT 'Moto', plate TEXT DEFAULT '', available INTEGER NOT NULL DEFAULT 0, active INTEGER NOT NULL DEFAULT 1);
CREATE TABLE IF NOT EXISTS orders(id INTEGER PRIMARY KEY, code TEXT UNIQUE NOT NULL, client_id INTEGER NOT NULL REFERENCES users(id), restaurant_id INTEGER NOT NULL REFERENCES restaurants(id), courier_id INTEGER REFERENCES couriers(id), status TEXT NOT NULL, subtotal REAL NOT NULL, delivery_fee REAL NOT NULL, total REAL NOT NULL, payment_method TEXT NOT NULL DEFAULT 'PIX', payment_status TEXT NOT NULL DEFAULT 'PENDENTE', address TEXT NOT NULL, notes TEXT DEFAULT '', created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS order_items(id INTEGER PRIMARY KEY, order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE, product_id INTEGER REFERENCES products(id), product_name TEXT NOT NULL, unit_price REAL NOT NULL, quantity INTEGER NOT NULL, subtotal REAL NOT NULL);
CREATE TABLE IF NOT EXISTS order_events(id INTEGER PRIMARY KEY, order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE, status TEXT NOT NULL, message TEXT NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS sessions(token TEXT PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, expires_at TEXT NOT NULL);
CREATE INDEX IF NOT EXISTS idx_orders_client ON orders(client_id);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant ON orders(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
"""
def connect():
    c=sqlite3.connect(DB, timeout=15); c.row_factory=sqlite3.Row; c.execute("PRAGMA foreign_keys=ON"); return c
def hash_pw(pw, salt=None):
    salt=salt or secrets.token_bytes(16)
    return salt.hex()+":"+hashlib.pbkdf2_hmac("sha256",pw.encode(),salt,240000).hex()
def verify_pw(pw, stored):
    try:
        s,h=stored.split(":"); return secrets.compare_digest(hashlib.pbkdf2_hmac("sha256",pw.encode(),bytes.fromhex(s),240000).hex(),h)
    except Exception: return False
def init_db():
    with connect() as c:
        c.executescript(SCHEMA)
        if not c.execute("SELECT 1 FROM users WHERE email='admin@wefood.local'").fetchone():
            c.execute("INSERT INTO users(name,email,phone,password_hash,role) VALUES(?,?,?,?,?)",("Administrador WeFood","admin@wefood.local","","", "ADMIN"))
            uid=c.execute("SELECT id FROM users WHERE email='admin@wefood.local'").fetchone()["id"]
            c.execute("UPDATE users SET password_hash=? WHERE id=?",(hash_pw("admin123"),uid))
        if c.execute("SELECT COUNT(*) n FROM restaurants").fetchone()["n"]==0:
            demo=[
              ("Burger Station","Hambúrgueres artesanais e combos","Centro","Rua das Flores, 120",5.99,25,25),
              ("Açaí do Bairro","Açaí, frutas e sobremesas","Cambuí","Av. Norte Sul, 500",3.99,15,20),
              ("Sabor Caseiro","Pratos feitos e comida brasileira","Taquaral","Rua das Palmeiras, 80",6.50,20,35)]
            for name,desc,city,address,fee,mn,prep in demo:
                c.execute("INSERT INTO restaurants(name,description,city,address,fee,min_order,prep_minutes) VALUES(?,?,?,?,?,?,?)",(name,desc,city,address,fee,mn,prep))
            rs=c.execute("SELECT id,name FROM restaurants").fetchall()
            menus={
              "Burger Station":[("Combo da casa","Burger, batata e bebida","Combos",25),("Burger artesanal","Pão, carne e queijo","Hambúrgueres",29.9),("Burger duplo","Dois hambúrgueres e molho","Hambúrgueres",36.9)],
              "Açaí do Bairro":[("Açaí 500ml","Açaí com acompanhamentos","Açaí",22),("Açaí grande","Açaí, frutas e granola","Açaí",32),("Creme de cupuaçu","Creme gelado","Sobremesas",18)],
              "Sabor Caseiro":[("Prato do dia","Arroz, feijão, proteína e salada","Pratos feitos",27.9),("Parmegiana","Acompanha arroz e fritas","Pratos feitos",34.9)]}
            for r in rs:
                for name,desc,cat,price in menus[r["name"]]:
                    c.execute("INSERT INTO products(restaurant_id,name,description,category,price) VALUES(?,?,?,?,?)",(r["id"],name,desc,cat,price))
        if c.execute("SELECT COUNT(*) n FROM couriers").fetchone()["n"]==0:
            c.execute("INSERT INTO users(name,email,phone,password_hash,role) VALUES(?,?,?,?,?)",("Entregador Demonstração","entregador@wefood.local","(19) 99999-0000",hash_pw("123456"),"COURIER"))
            uid=c.execute("SELECT id FROM users WHERE email='entregador@wefood.local'").fetchone()["id"]
            c.execute("INSERT INTO couriers(user_id,name,phone,vehicle,available) VALUES(?,?,?,?,1)",(uid,"Entregador Demonstração","(19) 99999-0000","Moto"))
def rowdict(r): return dict(r) if r else None
def clean_user(r):
    d=rowdict(r); d.pop("password_hash",None); return d
def money(v):
    try: return round(float(v),2)
    except: raise ValueError("Valor inválido.")
class APIError(Exception):
    def __init__(self,status,msg): self.status=status; self.msg=msg
class Handler(SimpleHTTPRequestHandler):
    def __init__(self,*a,**kw): super().__init__(*a,directory=str(WEB),**kw)
    def log_message(self,fmt,*args): print("[WeFood]",fmt%args)
    def send_json(self,status,data):
        raw=json.dumps(data,ensure_ascii=False).encode()
        self.send_response(status); self.send_header("Content-Type","application/json; charset=utf-8"); self.send_header("Content-Length",str(len(raw))); self.send_header("Access-Control-Allow-Origin","*"); self.send_header("Access-Control-Allow-Headers","Content-Type, Authorization"); self.send_header("Access-Control-Allow-Methods","GET, POST, PATCH, DELETE, OPTIONS"); self.end_headers(); self.wfile.write(raw)
    def body(self):
        n=int(self.headers.get("Content-Length",0))
        if n>1_000_000: raise APIError(413,"Requisição muito grande.")
        try: return json.loads(self.rfile.read(n) or b"{}")
        except: raise APIError(400,"JSON inválido.")
    def auth(self,c,roles=None):
        token=self.headers.get("Authorization","").replace("Bearer ","")
        row=c.execute("SELECT u.* FROM sessions s JOIN users u ON u.id=s.user_id WHERE s.token=? AND s.expires_at>?",(token,datetime.now(timezone.utc).isoformat())).fetchone()
        if not row: raise APIError(401,"Faça login para continuar.")
        if roles and row["role"] not in roles: raise APIError(403,"Você não tem permissão para esta ação.")
        return row
    def do_OPTIONS(self): self.send_json(200,{"ok":True})
    def do_GET(self): self.route("GET")
    def do_POST(self): self.route("POST")
    def do_PATCH(self): self.route("PATCH")
    def do_DELETE(self): self.route("DELETE")
    def route(self,method):
      try:
        path=urlparse(self.path).path.rstrip("/") or "/"
        if not path.startswith("/api"):
          if method=="GET": return SimpleHTTPRequestHandler.do_GET(self)
          raise APIError(404,"Rota não encontrada.")
        data=self.body() if method in ("POST","PATCH") else {}
        with connect() as c:
          if path=="/api/health": return self.send_json(200,{"ok":True,"database":"sqlite","time":datetime.now().isoformat()})
          if path=="/api/auth/register" and method=="POST":
            name=str(data.get("name","")).strip(); email=str(data.get("email","")).strip().lower(); pw=str(data.get("password",""))
            role=data.get("role","CLIENT")
            if role not in ("CLIENT","RESTAURANT","COURIER"): raise APIError(400,"Tipo de conta inválido.")
            if len(name)<2 or not re.match(r"^[^@\s]+@[^@\s]+\.[^@\s]+$",email) or len(pw)<6: raise APIError(400,"Informe nome, e-mail válido e senha com pelo menos 6 caracteres.")
            try: c.execute("INSERT INTO users(name,email,phone,password_hash,role) VALUES(?,?,?,?,?)",(name,email,data.get("phone",""),hash_pw(pw),role))
            except sqlite3.IntegrityError: raise APIError(409,"Este e-mail já está cadastrado.")
            uid=c.execute("SELECT id FROM users WHERE email=?",(email,)).fetchone()["id"]
            if role=="COURIER": c.execute("INSERT INTO couriers(user_id,name,phone,vehicle,plate,available) VALUES(?,?,?,?,?,0)",(uid,name,data.get("phone",""),data.get("vehicle","Moto"),data.get("plate","")))
            if role=="RESTAURANT":
              rid=c.execute("INSERT INTO restaurants(owner_id,name,description,phone,city,address,fee,min_order,approved) VALUES(?,?,?,?,?,?,?,?,1)",(uid,data.get("restaurant_name",name),data.get("description",""),data.get("phone",""),data.get("city","Campinas"),data.get("address",""),money(data.get("fee",0)),money(data.get("min_order",0)))).lastrowid
            token=secrets.token_urlsafe(32); c.execute("INSERT INTO sessions VALUES(?,?,?)",(token,uid,(datetime.now(timezone.utc)+timedelta(days=7)).isoformat()))
            return self.send_json(201,{"token":token,"user":clean_user(c.execute("SELECT * FROM users WHERE id=?",(uid,)).fetchone())})
          if path=="/api/auth/login" and method=="POST":
            u=c.execute("SELECT * FROM users WHERE email=? AND active=1",(str(data.get("email","")).lower(),)).fetchone()
            if not u or not verify_pw(str(data.get("password","")),u["password_hash"]): raise APIError(401,"E-mail ou senha incorretos.")
            token=secrets.token_urlsafe(32); c.execute("INSERT INTO sessions VALUES(?,?,?)",(token,u["id"],(datetime.now(timezone.utc)+timedelta(days=7)).isoformat()))
            return self.send_json(200,{"token":token,"user":clean_user(u)})
          if path=="/api/auth/logout" and method=="POST":
            token=self.headers.get("Authorization","").replace("Bearer ",""); c.execute("DELETE FROM sessions WHERE token=?",(token,)); return self.send_json(200,{"ok":True})
          if path=="/api/me":
            u=self.auth(c); return self.send_json(200,clean_user(u))
          if path=="/api/restaurants" and method=="GET":
            q=parse_qs(urlparse(self.path).query).get("q",[""])[0]
            rs=c.execute("SELECT r.*,0.0 AS rating FROM restaurants r WHERE r.active=1 AND r.approved=1 AND (r.name LIKE ? OR r.description LIKE ? OR r.city LIKE ?) ORDER BY r.name",tuple([f"%{q}%"]*3)).fetchall()
            return self.send_json(200,[rowdict(x) for x in rs])
          if path=="/api/couriers" and method=="GET":
            self.auth(c,["ADMIN"]); return self.send_json(200,[rowdict(x) for x in c.execute("SELECT d.*,u.email FROM couriers d LEFT JOIN users u ON u.id=d.user_id ORDER BY d.id DESC")])
          if path=="/api/couriers" and method=="POST":
            self.auth(c,["ADMIN"]); name=str(data.get("name","")).strip(); email=str(data.get("email","")).strip().lower(); password=str(data.get("password",""))
            if len(name)<2 or not re.match(r"^[^@\s]+@[^@\s]+\.[^@\s]+$",email) or len(password)<6:
              raise APIError(400,"Informe nome, e-mail válido e senha com pelo menos 6 caracteres.")
            try:
              uid=c.execute("INSERT INTO users(name,email,phone,password_hash,role) VALUES(?,?,?,?,?)",(name,email,data.get("phone",""),hash_pw(password),"COURIER")).lastrowid
            except sqlite3.IntegrityError: raise APIError(409,"Este e-mail já está cadastrado.")
            cid=c.execute("INSERT INTO couriers(user_id,name,phone,vehicle,plate,available) VALUES(?,?,?,?,?,?)",(uid,name,data.get("phone",""),data.get("vehicle","Moto"),data.get("plate",""),int(bool(data.get("available",True))))).lastrowid
            return self.send_json(201,{"id":cid,"user_id":uid})
          if path=="/api/courier/availability" and method=="PATCH":
            u=self.auth(c,["COURIER"]); c.execute("UPDATE couriers SET available=? WHERE user_id=?",(int(bool(data.get("available"))),u["id"])); return self.send_json(200,{"ok":True})
          if path=="/api/orders" and method=="POST":
            u=self.auth(c,["CLIENT"]); rid=int(data.get("restaurant_id",0)); items=data.get("items",[])
            r=c.execute("SELECT * FROM restaurants WHERE id=? AND active=1 AND approved=1 AND is_open=1",(rid,)).fetchone()
            if not r: raise APIError(400,"Restaurante indisponível.")
            if not items: raise APIError(400,"O carrinho está vazio.")
            subtotal=0; valid=[]
            for it in items:
              p=c.execute("SELECT * FROM products WHERE id=? AND restaurant_id=? AND available=1",(int(it.get("product_id",0)),rid)).fetchone()
              qty=int(it.get("quantity",0))
              if not p or qty<1 or qty>50: raise APIError(400,"Produto indisponível ou quantidade inválida.")
              sub=round(p["price"]*qty,2); subtotal+=sub; valid.append((p,qty,sub))
            subtotal=round(subtotal,2)
            if subtotal<r["min_order"]: raise APIError(400,f"Pedido mínimo: R$ {r['min_order']:.2f}.")
            fee=r["fee"]; total=round(subtotal+fee,2)
            code="WF"+secrets.token_hex(3).upper()
            oid=c.execute("INSERT INTO orders(code,client_id,restaurant_id,status,subtotal,delivery_fee,total,payment_method,payment_status,address,notes) VALUES(?,?,?,'RECEBIDO',?,?,?,?,?,?,?)",(code,u["id"],rid,subtotal,fee,total,data.get("payment_method","PIX"),"SIMULADO_APROVADO",str(data.get("address","Endereço não informado")),str(data.get("notes","")))).lastrowid
            for p,qty,sub in valid: c.execute("INSERT INTO order_items(order_id,product_id,product_name,unit_price,quantity,subtotal) VALUES(?,?,?,?,?,?)",(oid,p["id"],p["name"],p["price"],qty,sub))
            c.execute("INSERT INTO order_events(order_id,status,message) VALUES(?,?,?)",(oid,"RECEBIDO","Pedido enviado ao restaurante"))
            return self.send_json(201,{"id":oid,"code":code,"status":"RECEBIDO","total":total})
          if path=="/api/orders" and method=="GET":
            u=self.auth(c); sql="SELECT o.*,r.name restaurant,cu.name courier,cl.name client FROM orders o JOIN restaurants r ON r.id=o.restaurant_id JOIN users cl ON cl.id=o.client_id LEFT JOIN couriers cu ON cu.id=o.courier_id"
            if u["role"]=="CLIENT": rows=c.execute(sql+" WHERE o.client_id=? ORDER BY o.id DESC",(u["id"],)).fetchall()
            elif u["role"]=="RESTAURANT": rows=c.execute(sql+" JOIN restaurants own ON own.id=o.restaurant_id WHERE own.owner_id=? ORDER BY o.id DESC",(u["id"],)).fetchall()
            elif u["role"]=="COURIER": rows=c.execute(sql+" JOIN couriers dc ON dc.id=o.courier_id WHERE dc.user_id=? ORDER BY o.id DESC",(u["id"],)).fetchall()
            else: rows=c.execute(sql+" ORDER BY o.id DESC").fetchall()
            return self.send_json(200,[rowdict(x) for x in rows])
          m=re.match(r"^/api/restaurants/(\d+)(?:/(products|orders|open))?$",path)
          if m:
            rid=int(m.group(1)); sub=m.group(2)
            if method=="GET" and not sub:
              r=c.execute("SELECT * FROM restaurants WHERE id=?",(rid,)).fetchone()
              if not r: raise APIError(404,"Restaurante não encontrado.")
              return self.send_json(200,rowdict(r))
            if method=="GET" and sub=="products":
              return self.send_json(200,[rowdict(x) for x in c.execute("SELECT * FROM products WHERE restaurant_id=? ORDER BY category,name",(rid,))])
            if sub=="products" and method=="POST":
              u=self.auth(c,["ADMIN","RESTAURANT"]); self.own_restaurant(c,u,rid)
              name=str(data.get("name","")).strip(); price=money(data.get("price",0))
              if not name or price<=0: raise APIError(400,"Nome e preço maior que zero são obrigatórios.")
              pid=c.execute("INSERT INTO products(restaurant_id,name,description,category,price,available) VALUES(?,?,?,?,?,?)",(rid,name,data.get("description",""),data.get("category","Outros"),price,int(bool(data.get("available",True))))).lastrowid
              return self.send_json(201,{"id":pid})
            if sub=="open" and method=="PATCH":
              u=self.auth(c,["ADMIN","RESTAURANT"]); self.own_restaurant(c,u,rid)
              c.execute("UPDATE restaurants SET is_open=? WHERE id=?",(int(bool(data.get("is_open"))),rid)); return self.send_json(200,{"ok":True})
            if sub=="orders" and method=="GET":
              u=self.auth(c,["ADMIN","RESTAURANT"]); self.own_restaurant(c,u,rid)
              rows=c.execute("SELECT o.*,u.name client FROM orders o JOIN users u ON u.id=o.client_id WHERE restaurant_id=? ORDER BY id DESC",(rid,)).fetchall()
              return self.send_json(200,[rowdict(x) for x in rows])
          m=re.match(r"^/api/products/(\d+)$",path)
          if m and method=="PATCH":
            u=self.auth(c,["ADMIN","RESTAURANT"]); p=c.execute("SELECT * FROM products WHERE id=?",(int(m.group(1)),)).fetchone()
            if not p: raise APIError(404,"Produto não encontrado.")
            self.own_restaurant(c,u,p["restaurant_id"])
            fields=[]; vals=[]
            for key,col in [("name","name"),("description","description"),("category","category"),("available","available"),("price","price")]:
              if key in data: fields.append(col+"=?"); vals.append(int(bool(data[key])) if key=="available" else money(data[key]) if key=="price" else data[key])
            if fields: c.execute("UPDATE products SET "+",".join(fields)+" WHERE id=?",(*vals,p["id"]))
            return self.send_json(200,{"ok":True})
          if m and method=="DELETE":
            u=self.auth(c,["ADMIN","RESTAURANT"]); p=c.execute("SELECT * FROM products WHERE id=?",(int(m.group(1)),)).fetchone()
            if not p: raise APIError(404,"Produto não encontrado.")
            self.own_restaurant(c,u,p["restaurant_id"]); c.execute("DELETE FROM products WHERE id=?",(p["id"],)); return self.send_json(200,{"ok":True})
          m=re.match(r"^/api/orders/(\d+)/(status|assign)$",path)
          if m and method=="PATCH":
            oid=int(m.group(1)); action=m.group(2); order=c.execute("SELECT * FROM orders WHERE id=?",(oid,)).fetchone()
            if not order: raise APIError(404,"Pedido não encontrado.")
            u=self.auth(c)
            if action=="status":
              new=data.get("status")
              if u["role"]=="RESTAURANT": self.own_restaurant(c,u,order["restaurant_id"])
              elif u["role"]=="COURIER":
                d=c.execute("SELECT id FROM couriers WHERE user_id=?",(u["id"],)).fetchone()
                if not d or order["courier_id"]!=d["id"]: raise APIError(403,"Este pedido não está atribuído a você.")
              elif u["role"]!="ADMIN": raise APIError(403,"Sem permissão.")
              allowed={"RECEBIDO":["PREPARANDO","CANCELADO"],"PREPARANDO":["PRONTO","CANCELADO"],"PRONTO":["SAIU_PARA_ENTREGA"],"SAIU_PARA_ENTREGA":["ENTREGUE"],"ENTREGUE":[],"CANCELADO":[]}
              if new not in allowed.get(order["status"],[]): raise APIError(400,"Transição de status não permitida.")
              if new=="SAIU_PARA_ENTREGA" and not order["courier_id"]: raise APIError(400,"Atribua um entregador antes de despachar.")
              c.execute("UPDATE orders SET status=?,updated_at=CURRENT_TIMESTAMP WHERE id=?",(new,oid))
              c.execute("INSERT INTO order_events(order_id,status,message) VALUES(?,?,?)",(oid,new,"Status atualizado para "+new))
              if new=="ENTREGUE" and order["courier_id"]:
                c.execute("UPDATE couriers SET available=1 WHERE id=?",(order["courier_id"],))
              return self.send_json(200,{"ok":True,"status":new})
            if action=="assign":
              self.auth(c,["ADMIN"]); cid=int(data.get("courier_id",0))
              courier=c.execute("SELECT * FROM couriers WHERE id=? AND active=1",(cid,)).fetchone()
              if not courier or not courier["available"]: raise APIError(400,"Entregador não está disponível.")
              if order["status"] not in ("PRONTO","PREPARANDO"): raise APIError(400,"Pedido ainda não está pronto para atribuição.")
              c.execute("UPDATE orders SET courier_id=? WHERE id=?",(cid,oid)); c.execute("UPDATE couriers SET available=0 WHERE id=?",(cid,))
              c.execute("INSERT INTO order_events(order_id,status,message) VALUES(?,?,?)",(oid,order["status"],"Entregador "+courier["name"]+" atribuído ao pedido"))
              return self.send_json(200,{"ok":True})
          if path=="/api/admin/summary":
            self.auth(c,["ADMIN"])
            return self.send_json(200,{"clients":c.execute("SELECT COUNT(*) FROM users WHERE role='CLIENT'").fetchone()[0],"restaurants":c.execute("SELECT COUNT(*) FROM restaurants").fetchone()[0],"couriers":c.execute("SELECT COUNT(*) FROM couriers").fetchone()[0],"orders":c.execute("SELECT COUNT(*) FROM orders").fetchone()[0],"revenue":c.execute("SELECT COALESCE(SUM(total),0) FROM orders WHERE status!='CANCELADO'").fetchone()[0]})
          if path=="/api/admin/users" and method=="GET":
            self.auth(c,["ADMIN"]); return self.send_json(200,[clean_user(x) for x in c.execute("SELECT * FROM users ORDER BY id DESC")])
          if path=="/api/admin/restaurants" and method=="GET":
            self.auth(c,["ADMIN"]); return self.send_json(200,[rowdict(x) for x in c.execute("SELECT * FROM restaurants ORDER BY id DESC")])
          if path=="/api/admin/restaurants" and method=="POST":
            self.auth(c,["ADMIN"]); rid=c.execute("INSERT INTO restaurants(name,description,city,address,fee,min_order) VALUES(?,?,?,?,?,?)",(data.get("name",""),data.get("description",""),data.get("city","Campinas"),data.get("address",""),money(data.get("fee",0)),money(data.get("min_order",0)))).lastrowid; return self.send_json(201,{"id":rid})
          if path=="/api/courier/orders" and method=="GET":
            u=self.auth(c,["COURIER"]); d=c.execute("SELECT id FROM couriers WHERE user_id=?",(u["id"],)).fetchone()
            if not d: return self.send_json(200,[])
            return self.send_json(200,[rowdict(x) for x in c.execute("SELECT o.*,r.name restaurant FROM orders o JOIN restaurants r ON r.id=o.restaurant_id WHERE courier_id=? ORDER BY id DESC",(d["id"],))])
          if path=="/api/order-events" and method=="GET":
            u=self.auth(c); oid=int(parse_qs(urlparse(self.path).query).get("order_id",[0])[0])
            o=c.execute("SELECT * FROM orders WHERE id=?",(oid,)).fetchone()
            if not o: raise APIError(404,"Pedido não encontrado.")
            if u["role"]=="CLIENT" and o["client_id"]!=u["id"]: raise APIError(403,"Sem permissão.")
            return self.send_json(200,[rowdict(x) for x in c.execute("SELECT * FROM order_events WHERE order_id=? ORDER BY id",(oid,))])
          raise APIError(404,"Rota não encontrada.")
      except APIError as e: self.send_json(e.status,{"error":e.msg})
      except (ValueError,TypeError) as e: self.send_json(400,{"error":str(e)})
      except Exception as e:
        print("ERRO:",repr(e)); self.send_json(500,{"error":"Erro interno. Consulte o terminal do servidor."})
    def own_restaurant(self,c,u,rid):
      if u["role"]=="ADMIN": return
      r=c.execute("SELECT owner_id FROM restaurants WHERE id=?",(rid,)).fetchone()
      if not r or r["owner_id"]!=u["id"]: raise APIError(403,"Você não é responsável por este restaurante.")
def main():
    init_db()
    print(f"WeFood API em http://127.0.0.1:{PORT}")
    print("Admin: admin@wefood.local / admin123")
    print("Demo courier: entregador@wefood.local / 123456")
    ThreadingHTTPServer(("0.0.0.0",PORT),Handler).serve_forever()
if __name__=="__main__": main()
