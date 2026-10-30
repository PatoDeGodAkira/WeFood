# WeFood — versão local funcional

Esta entrega adiciona um backend executável e um painel web local ao projeto Android enviado. O app Android original e seus recursos visuais foram preservados; a nova camada local permite testar o fluxo completo de delivery no navegador do próprio computador.

## Requisitos
- Windows 10/11 ou Linux/macOS
- Python 3.10 ou superior
- Não precisa instalar bibliotecas Python, MySQL, Docker ou criar conta em serviços externos.

## Como executar no Windows
1. Extraia a pasta `WeFoodAndroid` do ZIP.
2. Dê dois cliques em `INICIAR_WEFOOD.bat`.
3. Aguarde aparecer `WeFood API em http://127.0.0.1:8080`.
4. Abra `http://127.0.0.1:8080` no navegador.

No Linux/macOS: execute `python3 backend/server.py` e abra o mesmo endereço.

O banco `backend/wefood.db` é criado automaticamente e persiste os dados entre execuções. Para reiniciar o ambiente de demonstração, pare o servidor e apague esse arquivo; ele será recriado com os dados iniciais.

## Contas iniciais
- Administrador: `admin@wefood.local` / `admin123`
- Entregador demonstração: `entregador@wefood.local` / `123456`

Crie as contas de cliente e restaurante pela opção **Criar conta**. Para restaurante, o registro cria também a loja vinculada à conta. O cadastro de entregador cria seu perfil de entregas.

## Fluxo completo de teste
1. Entre como cliente (ou crie uma conta).
2. Abra Restaurantes, escolha uma loja, adicione produtos e finalize um pedido.
3. Entre na conta da loja que recebeu o pedido. Em Pedidos, inicie o preparo e marque como pronto.
4. Entre como administrador e abra Pedidos. Atribua o pedido a um entregador disponível.
5. Entre na conta do entregador, deixe-se disponível e acompanhe as entregas atribuídas. O administrador atribui entregadores pelo ID listado na tela.
6. O restaurante ou administrador pode despachar o pedido depois da atribuição; o entregador confirma a entrega na sua área.
7. O cliente consulta Meus pedidos para verificar o status.

## O que está implementado
- API HTTP JSON em `/api`, com persistência SQLite.
- Cadastro/login/logout com hash PBKDF2 de senha e token de sessão.
- Perfis e permissões: cliente, restaurante, entregador e administrador.
- Cadastro de restaurante junto com conta do parceiro; abertura/fechamento da loja.
- Cadastro e disponibilidade de produtos; preço e estoque lógico (disponível/pausado).
- Carrinho no painel do cliente; o servidor recalcula preço, subtotal, taxa e total usando os valores do banco.
- Criação de pedido e snapshot dos itens/preços.
- Fila de pedidos, transições de status validadas e histórico de eventos.
- Cadastro e disponibilidade de entregadores; atribuição administrativa e confirmação de entrega.
- Painel administrativo com indicadores e listagem de usuários, restaurantes e entregadores.
- Dados de demonstração iniciais para testar sem configurar catálogo manualmente.

## Limites honestos desta versão
- O pagamento é **simulado** (`SIMULADO_APROVADO`); não há cobrança real, Pix real ou cartão. Não insira dados reais de cartão.
- Não há integração com Google Maps, geolocalização em tempo real, SMS, e-mail ou push.
- O navegador local é a interface funcional desta entrega. As telas Compose do APK original ainda são o protótipo Android e não foram integralmente migradas para consumir esta API.
- O servidor usa HTTP local e CORS aberto para desenvolvimento. Não publique na internet sem HTTPS, gestão de segredos, rate limiting, política de privacidade/LGPD, backups, logs e revisão de segurança.
- O banco local é SQLite para facilitar execução no PC. O arquivo SQL MySQL original foi mantido como referência; não é executado pelo servidor local.

## API principal
- `GET /api/health`
- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`, `GET /api/me`
- `GET /api/restaurants`, `GET /api/restaurants/{id}`, `GET /api/restaurants/{id}/products`
- `POST /api/orders`, `GET /api/orders`
- `PATCH /api/orders/{id}/status`, `PATCH /api/orders/{id}/assign`
- `POST /api/restaurants/{id}/products`, `PATCH /api/products/{id}`, `DELETE /api/products/{id}`
- `PATCH /api/restaurants/{id}/open`
- `GET /api/couriers`, `POST /api/couriers`, `PATCH /api/courier/availability`, `GET /api/courier/orders`
- `GET /api/admin/summary`, `GET /api/admin/users`, `GET /api/admin/restaurants`

A autenticação das rotas protegidas usa `Authorization: Bearer <token>`.

## Estrutura
- `backend/server.py` — servidor, regras de negócio e API.
- `backend/web/index.html` — painel web local.
- `backend/wefood.db` — banco SQLite gerado em tempo de execução.
- `database/WeFood.sql` e `database/WeFood_melhorias.sql` — SQL MySQL de referência do projeto.
