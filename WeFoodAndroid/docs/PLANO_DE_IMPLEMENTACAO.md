# WeFood — implementação local concluída e próximos limites

## O que foi implementado nesta entrega
- API HTTP JSON executável em `backend/server.py`, sem dependências Python externas.
- Persistência SQLite, criada automaticamente no primeiro uso.
- Cadastro/login/logout, hash de senha PBKDF2 e sessões por token.
- Perfis CLIENT, RESTAURANT, COURIER e ADMIN.
- Cadastro de restaurante vinculado à conta do parceiro.
- CRUD de produtos e controle de disponibilidade.
- Criação de pedidos com preço, taxa e total recalculados pelo servidor e snapshot dos itens.
- Fluxo de status validado: RECEBIDO → PREPARANDO → PRONTO → SAIU_PARA_ENTREGA → ENTREGUE; cancelamento permitido antes da expedição.
- Histórico de eventos do pedido.
- Cadastro e disponibilidade de entregadores, atribuição pelo administrador e confirmação de entrega pelo entregador.
- Painel web local para operar os quatro perfis.
- Dados iniciais de demonstração e launcher para Windows/Linux.

## Arquitetura nesta fase
- Cliente web local: `backend/web/index.html`.
- API: servidor HTTP Python em `backend/server.py`.
- Banco: SQLite em `backend/wefood.db` (criado quando o servidor iniciar).
- Porta padrão: 8080; pode ser alterada com a variável de ambiente `PORT`.
- Arquivo de banco customizado: variável `WEFOOD_DB`.

## Fluxo de teste
1. Cliente cria conta, seleciona restaurante e cria pedido.
2. Restaurante entra com sua conta, atualiza pedido para PREPARANDO e depois PRONTO.
3. Administrador atribui um entregador disponível.
4. Entregador inicia a entrega e confirma ENTREGUE.
5. Cliente consulta o pedido e vê o status atualizado.

## O que permanece demonstrativo / não integrado
- As telas Android Compose existentes ainda não consomem esta API. O painel web é a interface integrada desta entrega para uso no PC.
- Pagamento usa aprovação simulada, sem cobrança ou integração com banco/provedor.
- Sem geolocalização GPS, mapas de rota, push, SMS, e-mail ou upload de imagens.
- A criação de restaurante pelo parceiro é imediata nesta versão local; não há fila de aprovação.
- SQLite é a opção de desenvolvimento local. A estrutura MySQL fornecida permanece preservada, mas não é usada pelo servidor local.

## Antes de publicar ou usar com clientes reais
- Migrar a API para framework e servidor de produção, com HTTPS.
- Implementar autorização e auditoria com testes automatizados, rate limiting, gestão segura de sessão e proteção CSRF conforme a arquitetura de implantação.
- Adicionar aprovação administrativa, políticas de cancelamento/reembolso, idempotência e reconciliação de pagamento.
- Integrar gateway em sandbox e webhooks assinados; nunca confiar no status de pagamento enviado pelo cliente.
- Integrar mapas/localização com consentimento e retenção mínima, notificações e armazenamento seguro de imagens.
- Ligar o aplicativo Android à API com camada Repository/ViewModel, tratamento de falhas e armazenamento seguro de token.
- Validar LGPD, backup, restauração e monitoramento.
