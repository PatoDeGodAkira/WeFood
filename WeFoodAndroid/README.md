# WeFood — Android + ambiente local funcional

O projeto contém o aplicativo Android original em Kotlin/Jetpack Compose e um backend/API com painel web local para executar o fluxo de delivery no computador.

## Executar o sistema funcional no PC
Leia `README_LOCAL.md` e execute `INICIAR_WEFOOD.bat` (Windows) ou `INICIAR_WEFOOD.sh` (Linux/macOS). O painel abre em `http://127.0.0.1:8080`. O servidor cria o banco SQLite automaticamente.

Contas de demonstração:
- Administrador: `admin@wefood.local` / `admin123`
- Entregador: `entregador@wefood.local` / `123456`

Cliente e restaurante: cadastre pela tela inicial do painel.

## Abrir o aplicativo Android
1. Abra esta pasta no Android Studio.
2. Use JDK 17 e sincronize o Gradle.
3. Execute o módulo `app` em um emulador ou dispositivo Android 7.0 (API 24) ou superior.

## O que está no projeto
- `app/`: protótipo Android Kotlin/Jetpack Compose e seus recursos visuais.
- `backend/server.py`: API HTTP, autenticação, regras de negócio e banco SQLite.
- `backend/web/index.html`: painel web responsivo para cliente, restaurante, entregador e administrador.
- `database/WeFood.sql`: schema MySQL original fornecido.
- `database/WeFood_melhorias.sql`: migração MySQL complementar original.
- `README_LOCAL.md`: instruções completas, contas e fluxo de teste.

## Estado e limitações
O painel web local está conectado ao backend e permite que os quatro perfis compartilhem os mesmos cadastros e pedidos persistidos. O app Android Compose original foi preservado, mas ainda não foi migrado para consumir a API; suas telas continuam demonstrativas e isoladas do banco local.

O pagamento é simulado, sem cobrança real. Não há integração externa de mapas, GPS em tempo real, e-mail, SMS ou notificações push. Para produção, ainda são necessários HTTPS, revisão de segurança, backups, gestão de segredos, política LGPD e integração de um gateway de pagamento homologado. O banco SQLite local foi escolhido para facilitar a execução no PC; o schema MySQL recebido foi preservado como referência.
