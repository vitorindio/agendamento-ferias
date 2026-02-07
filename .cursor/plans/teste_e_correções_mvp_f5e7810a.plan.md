---
name: Teste e Correções MVP
overview: Plano para testar o sistema localmente, corrigir bugs identificados na revisao de codigo e implementar melhorias de estabilidade antes de seguir com novas features.
todos:
  - id: run-local
    content: Rodar sistema localmente (Docker + Backend + Frontend)
    status: pending
  - id: fix-axios-pinia
    content: Corrigir bug do Pinia fora de contexto no axios.js
    status: pending
  - id: fix-jpql-query
    content: Corrigir query JPQL e excluir REJEITADO de conflitos
    status: pending
  - id: fix-calendario-reatividade
    content: Corrigir reatividade do CalendarioAnual.vue
    status: pending
  - id: fix-badge-pendentes
    content: Carregar pendentes ao montar MainLayout
    status: pending
  - id: fix-n1-queries
    content: Resolver N+1 queries no SolicitacaoService
    status: pending
  - id: add-validations
    content: Adicionar validacoes no RegisterRequest
    status: pending
  - id: manual-testing
    content: Executar checklist de testes manuais
    status: pending
---

# Plano de Continuidade - Sistema de Gestao de Ferias

## Visao Geral

O projeto esta bem estruturado e segue boas praticas. O `@EnableAsync` e o JWT em Base64 ja foram corrigidos conforme o CONTEXT.md. No entanto, identifiquei **6 bugs criticos** e **5 melhorias importantes** que devem ser tratados antes de prosseguir.

---

## Fase 1: Rodar o Sistema Local (Validacao Inicial)

Antes de corrigir bugs, precisamos validar que o sistema sobe corretamente:

```bash
# 1. Subir PostgreSQL
docker-compose up -d

# 2. Backend (em um terminal)
cd backend
./mvnw spring-boot:run

# 3. Frontend (em outro terminal)
cd frontend
npm install
npm run dev
```

**Testar manualmente:**

- Login com `admin@empresa.com` / `admin123`
- Login com `maria@empresa.com` / `123456` (gestor)
- Criar uma solicitacao de ferias
- Aprovar como gestor

---

## Fase 2: Bugs Criticos a Corrigir

### Bug 1: Pinia fora de contexto no Axios

**Arquivo:** [`frontend/src/api/axios.js`](frontend/src/api/axios.js)

O `useAuthStore()` e chamado fora do ciclo de vida do Vue, o que pode causar erro "getActivePinia() was called but there was no active Pinia".

**Solucao:** Acessar o token diretamente do localStorage no interceptor.

### Bug 2: Query JPQL com string literal

**Arquivo:** [`backend/src/main/java/com/empresa/ferias/repository/SolicitacaoRepository.java`](backend/src/main/java/com/empresa/ferias/repository/SolicitacaoRepository.java)

```java
// Problema: s.status != 'CANCELADO' (string literal)
// Deve usar: s.status != com.empresa.ferias.model.StatusSolicitacao.CANCELADO
```

Tambem falta excluir `REJEITADO` da verificacao de conflitos.

### Bug 3: Reatividade do CalendarioAnual

**Arquivo:** [`frontend/src/components/CalendarioAnual.vue`](frontend/src/components/CalendarioAnual.vue)

A computed `meses` nao rastreia corretamente `props.solicitacoes`. Ao receber novas solicitacoes, o calendario pode nao atualizar.

**Solucao:** Adicionar `props.solicitacoes` como dependencia explicita.

### Bug 4: Badge de pendentes nao carrega ao iniciar

**Arquivo:** [`frontend/src/layouts/MainLayout.vue`](frontend/src/layouts/MainLayout.vue)

O menu mostra badge de aprovacoes pendentes, mas `carregarPendentes()` nunca e chamado ao montar o layout.

### Bug 5: N+1 Queries no listarSolicitacoesEquipe

**Arquivo:** [`backend/src/main/java/com/empresa/ferias/service/SolicitacaoService.java`](backend/src/main/java/com/empresa/ferias/service/SolicitacaoService.java)

```java
// e.getMembros() causa lazy loading para cada equipe
List<Long> membroIds = equipes.stream()
    .flatMap(e -> e.getMembros().stream()) // N+1 aqui
```

**Solucao:** Usar `@EntityGraph` ou JOIN FETCH no repositorio.

### Bug 6: Solicitacao rejeitada pode ter conflito falso

A query `findConflitos` exclui apenas `CANCELADO`, mas uma solicitacao `REJEITADA` tambem nao deveria bloquear novas solicitacoes para o mesmo periodo.

---

## Fase 3: Melhorias de Estabilidade

### Melhoria 1: Validacoes no RegisterRequest

**Arquivo:** [`backend/src/main/java/com/empresa/ferias/dto/auth/RegisterRequest.java`](backend/src/main/java/com/empresa/ferias/dto/auth/RegisterRequest.java)

Adicionar:

- `@Size(min=6)` para senha
- `@NotBlank` para campos obrigatorios
- Validar formato de email

### Melhoria 2: Health Check Endpoint

Criar endpoint `GET /api/health` para monitoramento e validacao de conectividade.

### Melhoria 3: Tratamento de erro mais informativo

**Arquivo:** [`backend/src/main/java/com/empresa/ferias/config/GlobalExceptionHandler.java`](backend/src/main/java/com/empresa/ferias/config/GlobalExceptionHandler.java)

Adicionar handler para `DataIntegrityViolationException` e `JwtException`.

### Melhoria 4: Feedback visual no Frontend

- Adicionar loading spinners durante operacoes
- Usar toast notifications em vez de alerts

### Melhoria 5: Refresh do saldo apos aprovacao

Quando gestor aprova, o frontend da pessoa solicitante nao atualiza automaticamente (so na proxima vez que carregar).

---

## Fase 4: Checklist de Testes Manuais

| Cenario | Esperado |

|---------|----------|

| Login com credenciais invalidas | Mensagem "Email ou senha incorretos" |

| Login com conta nao ativada | Mensagem "Conta nao ativada" |

| Criar solicitacao sem saldo | Erro "Saldo insuficiente" |

| Criar solicitacao com conflito | Erro "Ja existe solicitacao" |

| Aprovar como funcionario | Erro 403 Forbidden |

| Aprovar solicitacao ja aprovada | Erro "Apenas pendentes podem ser aprovadas" |

| Cancelar solicitacao aprovada | Saldo deve ser devolvido |

---

## Arquivos Principais que Serao Alterados

1. [`frontend/src/api/axios.js`](frontend/src/api/axios.js) - Corrigir Pinia fora de contexto
2. [`frontend/src/components/CalendarioAnual.vue`](frontend/src/components/CalendarioAnual.vue) - Corrigir reatividade
3. [`frontend/src/layouts/MainLayout.vue`](frontend/src/layouts/MainLayout.vue) - Carregar pendentes ao montar
4. [`backend/.../repository/SolicitacaoRepository.java`](backend/src/main/java/com/empresa/ferias/repository/SolicitacaoRepository.java) - Corrigir query JPQL
5. [`backend/.../service/SolicitacaoService.java`](backend/src/main/java/com/empresa/ferias/service/SolicitacaoService.java) - EntityGraph para N+1
6. [`backend/.../dto/auth/RegisterRequest.java`](backend/src/main/java/com/empresa/ferias/dto/auth/RegisterRequest.java) - Adicionar validacoes

---

## Ordem de Execucao Recomendada

1. **Primeiro:** Rodar o sistema localmente e validar que funciona
2. **Segundo:** Corrigir bugs criticos (Bug 1-6)
3. **Terceiro:** Implementar melhorias de estabilidade
4. **Quarto:** Executar checklist de testes manuais
5. **Quinto:** Avancar para proximas features (edicao de solicitacao, paginacao, etc.)