# 🏖️ Sistema de Gestão de Férias e Ausências

Sistema completo para gerenciamento de férias e ausências de funcionários, com controle de saldos, aprovações por gestores e visualização em calendário anual.

> **Status:** MVP Funcional ✅ | **Última atualização:** 05/02/2026

## 🚀 Tecnologias

### Backend
- **Java 17** + **Spring Boot 3**
- **Spring Security** + **JWT** para autenticação
- **Spring Data JPA** + **PostgreSQL**
- **Spring Mail** para notificações por email

### Frontend
- **Vue.js 3** (Composition API)
- **Vite** para build
- **Tailwind CSS** para estilização
- **Pinia** para gerenciamento de estado
- **Vue Router** para navegação

## 📋 Funcionalidades

### Para Funcionários
- ✅ Visualizar saldo de férias
- ✅ Calendário anual com ausências marcadas
- ✅ Solicitar férias ou registrar ausências
- ✅ Acompanhar status das solicitações
- ✅ Cancelar solicitações pendentes

### Para Gestores
- ✅ Visualizar ausências da equipe
- ✅ Aprovar/Rejeitar solicitações
- ✅ Receber notificações de novas solicitações

### Tipos de Ausência
- 🏖️ Férias (deduz do saldo)
- 🏥 Licença Médica
- 👶 Licença Maternidade/Paternidade
- ☀️ Day Off
- 🏠 Home Office

## 🛠️ Como Executar

### Pré-requisitos
- Java 17+
- Node.js 18+
- Docker (para PostgreSQL)

### 1. Subir o Banco de Dados

```bash
docker-compose up -d
```

### 2. Executar o Backend

```bash
cd backend
./mvnw spring-boot:run
# ou no Windows sem wrapper:
mvn spring-boot:run
```

> **Nota:** Requer Java 17+. Se tiver múltiplas versões, configure `JAVA_HOME`:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
> ```

O backend estará disponível em: `http://localhost:8080`

### 3. Executar o Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend estará disponível em: `http://localhost:5173`

## 🔐 Credenciais de Teste

| Usuário | Email | Senha | Role |
|---------|-------|-------|------|
| Gestor | gestor@empresa.com | 123456 | GESTOR |
| João | joao@empresa.com | 123456 | USER |
| Ana | ana@empresa.com | 123456 | USER |
| Carlos | carlos@empresa.com | 123456 | USER |

> **Nota:** Sistema simplificado com 2 roles - USER (funcionário) e GESTOR (aprova solicitações)

## 📁 Estrutura do Projeto

```
sistema-ferias/
├── backend/
│   ├── src/main/java/com/empresa/ferias/
│   │   ├── config/          # Configurações (Security, etc)
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositórios
│   │   ├── security/        # JWT, Filters
│   │   └── service/         # Lógica de negócio
│   └── src/main/resources/
│       └── application.yml  # Configurações
├── frontend/
│   ├── src/
│   │   ├── api/             # Axios config
│   │   ├── assets/          # CSS
│   │   ├── components/      # Componentes Vue
│   │   ├── layouts/         # Layout principal
│   │   ├── router/          # Rotas
│   │   ├── stores/          # Pinia stores
│   │   └── views/           # Páginas
│   └── index.html
└── docker-compose.yml
```

## 🔧 Configurações

### Variáveis de Ambiente

```bash
# Banco de dados
POSTGRES_DB=sistema_ferias
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123

# JWT
JWT_SECRET=sua-chave-secreta

# Email (opcional)
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=senha-app
```

## 📦 Build para Produção

### Build do Frontend
```bash
cd frontend
npm run build
```

### Copiar para o Backend
```bash
cp -r frontend/dist/* backend/src/main/resources/static/
```

### Build do JAR
```bash
cd backend
./mvnw clean package
```

### Executar
```bash
java -jar target/sistema-ferias-1.0.0.jar
```

O sistema completo estará disponível em: `http://localhost:8080`

## 📄 API Endpoints

### Autenticação
- `POST /api/auth/register` - Cadastro
- `POST /api/auth/login` - Login
- `GET /api/auth/confirm?token=` - Confirmar email

### Usuários
- `GET /api/usuarios/me` - Perfil atual
- `GET /api/usuarios/me/saldo` - Saldo de férias

### Solicitações
- `POST /api/solicitacoes` - Criar solicitação
- `GET /api/solicitacoes/minhas` - Minhas solicitações
- `POST /api/solicitacoes/{id}/aprovar` - Aprovar (gestor)
- `POST /api/solicitacoes/{id}/rejeitar` - Rejeitar (gestor)
- `POST /api/solicitacoes/{id}/cancelar` - Cancelar

### Equipes
- `GET /api/equipes/minhas` - Minhas equipes
- `GET /api/equipes/gerenciadas` - Equipes que gerencio

## 📋 Roadmap / TODOs

### 🔴 Em Breve - CRUDs para RH
- [ ] Gestão de Funcionários (cadastrar, editar, ativar/desativar)
- [ ] Gestão de Saldos (configurar dias por funcionário/ano)
- [ ] Configurações do Sistema (dias mín/máx, antecedência)
- [ ] Cadastro de Feriados

### 🟡 Futuro
- [ ] Relatórios (férias vencendo, histórico)
- [ ] Edição de solicitações
- [ ] Reset de senha
- [ ] Integração com Google Calendar

### 🟢 Ideias
- [ ] App mobile (PWA)
- [ ] Notificações push
- [ ] Aprovação em cadeia
- [ ] Exportar para PDF

> Veja mais detalhes em [CONTEXT.md](CONTEXT.md)

## 📝 Licença

MIT License
