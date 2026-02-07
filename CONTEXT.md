# 📋 Contexto do Projeto - Sistema de Gestão de Férias

**Última atualização:** 05/02/2026  
**Status:** MVP Funcional - Testado e Funcionando ✅

---

## 🎯 Objetivo do Projeto

Sistema de gerenciamento de férias e ausências de funcionários, baseado no plano em `C:\Users\VitorIndio\Documents\Plano - App Agendamento de Férias.pdf`.

### Requisitos Principais
- ✅ Funcionários solicitam férias pelo sistema
- ✅ Gestores aprovam/rejeitam solicitações
- ✅ Confirmação de email no cadastro
- ✅ Calendário anual visual
- ✅ Deploy como pacote único JAR
- 🔄 **Simplificado:** Apenas 2 roles (USER e GESTOR) - qualquer gestor aprova qualquer solicitação

---

## ✅ O Que Foi Implementado

### Backend (Java 17 + Spring Boot 3)

#### Estrutura
```
backend/
├── src/main/java/com/empresa/ferias/
│   ├── config/
│   │   ├── ApplicationConfig.java      # UserDetailsService bean
│   │   ├── SecurityConfig.java         # JWT + CORS + Auth
│   │   ├── GlobalExceptionHandler.java # Tratamento de erros
│   │   └── DataInitializer.java        # Dados iniciais
│   ├── controller/
│   │   ├── AuthController.java         # /api/auth/**
│   │   ├── SolicitacaoController.java  # /api/solicitacoes/**
│   │   ├── UsuarioController.java      # /api/usuarios/**
│   │   ├── EquipeController.java       # /api/equipes/**
│   │   └── TipoAusenciaController.java # /api/tipos-ausencia/**
│   ├── dto/
│   │   ├── auth/ (RegisterRequest, LoginRequest, AuthResponse)
│   │   ├── SolicitacaoDTO.java, SolicitacaoRequest.java
│   │   ├── UsuarioDTO.java, EquipeDTO.java, SaldoFeriasDTO.java
│   ├── model/
│   │   ├── Usuario.java      # Implementa UserDetails
│   │   ├── Equipe.java       # Relacionamento N:N com Usuario
│   │   ├── Solicitacao.java  # Férias/Ausências
│   │   ├── SaldoFerias.java  # Saldo por ano
│   │   ├── TipoAusencia.java # Férias, Licença, etc.
│   │   ├── Role.java         # USER, GESTOR (simplificado)
│   │   └── StatusSolicitacao.java # PENDENTE, APROVADO, etc.
│   ├── repository/ (5 interfaces JPA)
│   ├── security/
│   │   ├── JwtService.java           # Geração/validação JWT
│   │   └── JwtAuthenticationFilter.java
│   └── service/
│       ├── AuthService.java          # Login, Register, Confirm
│       ├── SolicitacaoService.java   # CRUD + Aprovações
│       ├── UsuarioService.java       # Perfil + Saldo
│       ├── EquipeService.java        # Gestão de equipes
│       ├── TipoAusenciaService.java  
│       └── EmailService.java         # Notificações async
└── src/main/resources/
    └── application.yml
```

#### Funcionalidades Backend
- ✅ Autenticação JWT (login, register, confirm email)
- ✅ CRUD de Solicitações com validações
- ✅ Verificação de conflitos de datas
- ✅ Verificação de saldo de férias
- ✅ Aprovação/Rejeição por gestores
- ✅ Atualização automática de saldo
- ✅ Notificações por email (async)
- ✅ Dados iniciais (tipos ausência, usuários teste)
- ✅ CORS configurado para desenvolvimento

### Frontend (Vue 3 + Vite + Tailwind CSS)

#### Estrutura
```
frontend/
├── src/
│   ├── api/
│   │   └── axios.js          # Interceptors JWT
│   ├── assets/
│   │   └── main.css          # Tailwind + componentes custom
│   ├── components/
│   │   ├── SaldoCard.vue     # Card de estatística
│   │   ├── ProximasAusencias.vue
│   │   ├── MiniCalendario.vue    # Mês atual
│   │   ├── CalendarioAnual.vue   # 12 meses com tooltip
│   │   └── LegendaCores.vue      # Legenda tipos ausência
│   ├── layouts/
│   │   └── MainLayout.vue    # Sidebar + Topbar
│   ├── router/
│   │   └── index.js          # Rotas + guards
│   ├── stores/
│   │   ├── auth.js           # Pinia - autenticação
│   │   └── solicitacoes.js   # Pinia - solicitações/saldo
│   └── views/
│       ├── LoginView.vue
│       ├── RegisterView.vue
│       ├── ConfirmarEmailView.vue
│       ├── DashboardView.vue     # Cards + Mini calendário
│       ├── MinhaAgendaView.vue   # Calendário anual
│       ├── NovaSolicitacaoView.vue
│       ├── MinhasSolicitacoesView.vue
│       ├── EquipeView.vue        # Calendário da equipe
│       └── AprovacoesView.vue    # Aprovar/Rejeitar
├── index.html
├── package.json
├── vite.config.js        # Proxy para /api
├── tailwind.config.js
└── postcss.config.js
```

#### Funcionalidades Frontend
- ✅ Login/Cadastro/Confirmação de email
- ✅ Dashboard com saldo e mini calendário
- ✅ Calendário anual interativo (12 meses)
- ✅ Tooltip ao passar mouse nos dias
- ✅ Formulário de nova solicitação com preview
- ✅ Lista de solicitações com filtros
- ✅ Tela de equipe (para gestores)
- ✅ Tela de aprovações pendentes
- ✅ Sidebar responsiva
- ✅ Proteção de rotas por role
- ✅ Dark theme moderno

---

## 🔍 Revisão de Código - Pontos Identificados

### ⚠️ Issues a Corrigir

#### 1. JWT Secret não está em Base64 válido
**Arquivo:** `application.yml`
```yaml
jwt:
  secret: minha-chave-secreta-super-segura-para-jwt-token-sistema-ferias-2024
```
**Problema:** O JwtService usa `Decoders.BASE64.decode()`, mas a string não está em Base64.
**Solução:** Gerar chave em Base64 ou mudar a implementação para usar a string diretamente.

#### 2. Falta @Async na config
**Arquivo:** `EmailService.java` usa `@Async` mas não há `@EnableAsync` na aplicação.
**Solução:** Adicionar `@EnableAsync` no `SistemaFeriasApplication.java`

#### 3. Query de conflito pode ter edge case
**Arquivo:** `SolicitacaoRepository.java`
```java
// Não exclui a própria solicitação ao editar (se implementar edição)
List<Solicitacao> findConflitos(Long usuarioId, LocalDate inicio, LocalDate fim);
```

#### 4. Falta validação de status atual
**Arquivo:** `SolicitacaoService.java`
```java
// Aprovar uma solicitação já aprovada não deveria ser possível
public SolicitacaoDTO aprovar(Long solicitacaoId, Long aprovadorId) {
    // Falta: if (solicitacao.getStatus() != PENDENTE) throw...
}
```

#### 5. Calendário não reage a mudança de ano (minor)
**Arquivo:** `CalendarioAnual.vue` - computed `meses` depende de `props.ano` mas pode não recalcular se solicitações mudarem.

### 🟡 Melhorias Sugeridas

#### Backend
1. **Adicionar paginação** nas listas de solicitações
2. **Implementar refresh token** para sessões longas
3. **Criar endpoint de health check** `/api/health`
4. **Adicionar auditoria** (quem aprovou, quando)
5. **Implementar soft delete** em vez de status CANCELADO
6. **Criar testes unitários** para services

#### Frontend
1. **Adicionar loading states** mais visuais
2. **Implementar toast notifications** em vez de alerts
3. **Adicionar skeleton loaders** enquanto carrega
4. **Melhorar responsividade** para mobile
5. **Adicionar PWA** support para uso offline
6. **Implementar dark/light mode toggle**

#### Segurança
1. **Rate limiting** nos endpoints de auth
2. **Validação mais rigorosa** no RegisterRequest
3. **Password strength** validation
4. **HTTPS** em produção
5. **Sanitização** de inputs para prevenir XSS

---

## 🚀 Próximos Passos (TODOs)

### ✅ Concluído nessa sessão (05/02/2026)
- [x] Corrigir bug do Pinia fora de contexto no axios.js
- [x] Corrigir query JPQL (usar enum em vez de string literal)
- [x] Excluir REJEITADO da verificação de conflitos
- [x] Corrigir reatividade do CalendarioAnual.vue
- [x] Carregar pendentes ao montar MainLayout (badge)
- [x] Resolver N+1 queries no SolicitacaoService (JOIN FETCH)
- [x] Corrigir StackOverflowError no hashCode (Lombok + JPA bidirecional)
- [x] **Simplificar roles:** de 4 (FUNCIONARIO, GESTOR, RH, ADMIN) para 2 (USER, GESTOR)
- [x] **Simplificar aprovações:** qualquer gestor aprova qualquer solicitação (sem equipes)
- [x] Testar fluxo completo: criar solicitação → aprovar como gestor ✅

---

### 🔴 Prioridade Alta - CRUDs Essenciais para RH

#### Gestão de Funcionários (CRUD)
- [ ] **Listar funcionários** - tela admin para ver todos os usuários
- [ ] **Cadastrar funcionário** - RH pode cadastrar funcionários (além do auto-cadastro)
- [ ] **Editar funcionário** - alterar nome, cargo, data de admissão, role
- [ ] **Ativar/Desativar conta** - desligar funcionário sem deletar dados
- [ ] **Promover a Gestor** - mudar role de USER para GESTOR

#### Gestão de Saldos de Férias (CRUD)
- [ ] **Configurar saldo inicial** - RH define quantos dias o funcionário tem por ano
- [ ] **Ajustar saldo manualmente** - correções (ex: transferiu de outra empresa)
- [ ] **Visualizar histórico de saldo** - ver uso ao longo dos anos
- [ ] **Cálculo automático CLT** - 30 dias por ano, proporcional ao tempo de casa
- [ ] **Importar saldos em lote** - CSV/Excel para migração inicial

#### Configurações do Sistema
- [ ] **Dias mínimos por solicitação** - ex: mínimo 5 dias
- [ ] **Dias máximos por solicitação** - ex: máximo 30 dias
- [ ] **Antecedência mínima** - ex: solicitar com 15 dias de antecedência
- [ ] **Permitir férias parceladas** - dividir em até 3 períodos (CLT)

---

### 🟡 Prioridade Média

#### Tipos de Ausência (CRUD)
- [ ] Criar novos tipos de ausência
- [ ] Editar tipos existentes (nome, cor, deduz saldo)
- [ ] Desativar tipos (sem deletar)

#### Feriados (CRUD)
- [ ] Cadastrar feriados nacionais
- [ ] Cadastrar feriados locais/estaduais
- [ ] Feriados não contam como dia de férias
- [ ] Importar feriados de API pública

#### Funcionalidades Adicionais
- [ ] Edição de solicitação (antes de aprovar)
- [ ] Reset de senha
- [ ] Perfil do usuário editável
- [ ] Notificações in-app (além de email)

---

### 🟢 Prioridade Baixa

#### Relatórios
- [ ] Funcionários com férias vencendo (período concessivo)
- [ ] Histórico de solicitações por período
- [ ] Saldo geral da empresa
- [ ] Exportar para PDF/Excel

#### Integrações
- [ ] Google Calendar
- [ ] Microsoft Outlook
- [ ] Slack/Teams (notificações)
- [ ] SSO (Single Sign-On)

#### Extras
- [ ] App mobile (PWA ou nativo)
- [ ] Dark/Light mode toggle
- [ ] Períodos bloqueados (alta temporada)
- [ ] Aprovação em cadeia (gestor -> RH)

---

### 📝 Notas para Implementação

**Sobre o cálculo de férias CLT:**
- Funcionário tem direito a 30 dias após 12 meses de trabalho (período aquisitivo)
- Tem mais 12 meses para tirar essas férias (período concessivo)
- Pode parcelar em até 3 períodos (um deles com mín. 14 dias)
- Pode vender até 1/3 das férias (abono pecuniário)

**Perguntar para o RH:**
- Como controlam os saldos hoje? (planilha, sistema, papel?)
- Usam período aquisitivo/concessivo ou só saldo anual?
- Têm regras específicas da empresa além da CLT?
- Querem controlar abono pecuniário (venda de férias)?

---

## 🔑 Credenciais de Teste

| Email | Senha | Role | Descrição |
|-------|-------|------|-----------|
| gestor@empresa.com | 123456 | GESTOR | Pode aprovar qualquer solicitação |
| joao@empresa.com | 123456 | USER | Funcionário de teste |
| ana@empresa.com | 123456 | USER | Funcionária de teste |
| carlos@empresa.com | 123456 | USER | Funcionário de teste |

---

## 🛠️ Comandos Úteis

```bash
# Subir banco de dados
docker-compose up -d

# Backend
cd backend
./mvnw spring-boot:run

# Frontend (dev)
cd frontend
npm install
npm run dev

# Build produção (JAR único)
cd frontend && npm run build
cp -r dist/* ../backend/src/main/resources/static/
cd ../backend && ./mvnw clean package
java -jar target/sistema-ferias-1.0.0.jar
```

---

## 📁 Arquivos Importantes

| Arquivo | Descrição |
|---------|-----------|
| `docker-compose.yml` | PostgreSQL config |
| `backend/pom.xml` | Dependências Maven |
| `backend/src/main/resources/application.yml` | Config do Spring |
| `frontend/package.json` | Dependências npm |
| `frontend/vite.config.js` | Proxy e aliases |
| `README.md` | Documentação geral |

---

## 📊 Métricas do Projeto

- **Total de arquivos:** 70
- **Backend:** ~30 arquivos Java
- **Frontend:** ~20 arquivos Vue/JS
- **Linhas de código estimadas:** ~3000

---

## 💡 Decisões de Arquitetura

1. **JWT stateless** - Sem sessão no servidor, escalável
2. **Pinia** - State management reativo moderno
3. **Tailwind** - Utility-first CSS, fácil de customizar
4. **PostgreSQL** - Robusto, suporte a JSON se necessário
5. **Monorepo** - Frontend e backend no mesmo repositório
6. **JAR único** - Simplifica deploy (Spring serve Vue estático)

---

## 🐛 Bugs Conhecidos

1. **Emails não enviam** se SMTP não configurado (falha silenciosa - intencional em dev)
2. **Calendário pode ficar lento** com muitas solicitações (sem virtualização)

---

## 📅 Histórico de Sessões

### Sessão 05/02/2026
- Testado MVP completo localmente
- Corrigidos 6 bugs críticos identificados na revisão
- Simplificado sistema de roles (4 → 2)
- Removida lógica de equipes para aprovações
- Sistema funcionando end-to-end ✅

---

*Documento atualizado para continuidade entre sessões de desenvolvimento.*
