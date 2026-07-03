# AtendeAi — Arquitetura da Plataforma

## Visão Geral

Plataforma de atendimento inteligente com chatbot baseado em IA (Claude API), agendamento de serviços, base de conhecimento por cliente e histórico completo.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 3.x |
| Banco de dados | PostgreSQL 16 |
| Embeddings / RAG | pgvector (extensão PostgreSQL) |
| IA / LLM | Claude API (Anthropic) — claude-3-5-haiku para chatbot |
| Migrations | Flyway |
| Auth | Spring Security + JWT (Bearer Token) |
| Build | Maven |
| Containerização | Docker + Docker Compose |

---

## Módulos do Sistema

```
┌─────────────────────────────────────────────────────────┐
│                      AtendeAi API                        │
│                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐ │
│  │   Auth   │  │ Clientes │  │Agendament│  │Chatbot │ │
│  │  Module  │  │  Module  │  │  Module  │  │ Module │ │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘ │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐  │
│  │Knowledge │  │ Histórico│  │    Notificações       │  │
│  │  Base    │  │ Module   │  │      Module           │  │
│  └──────────┘  └──────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────────────────┘
         │                             │
         ▼                             ▼
   PostgreSQL + pgvector          Claude API
```

### 1. Auth
- Login com email/senha (JWT)
- Perfis: `ADMIN`, `ATTENDANT`, `CLIENT`
- Refresh token

### 2. Clientes (Customers)
- Cadastro e gerenciamento de clientes
- Dados: nome, contato, endereço, preferências
- Cada cliente tem sua própria base de conhecimento isolada

### 3. Agendamentos (Scheduling)
- Criação, edição e cancelamento de agendamentos
- Cronograma de atendimento (view semanal/mensal)
- Verificação de disponibilidade
- Status: `PENDING`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

### 4. Chatbot
- Integração com Claude API via HTTP
- Contexto de conversa persistido no banco
- RAG: busca na base de conhecimento do cliente antes de cada resposta
- Fluxos: entendimento do problema → agendamento → confirmação
- Ferramentas do Claude (tool use): `book_appointment`, `check_availability`, `get_client_info`

### 5. Base de Conhecimento (Knowledge Base)
- Documentos por cliente (FAQs, procedimentos, produtos/serviços)
- Vetorização com embeddings (pgvector)
- Busca semântica para alimentar o contexto do chatbot

### 6. Histórico
- Histórico de conversas por cliente
- Histórico de serviços realizados
- Timeline completa de interações

---

## Fluxo Principal do Chatbot

```
Cliente envia mensagem
        │
        ▼
  Busca contexto
  (histórico recente da conversa)
        │
        ▼
  Busca semântica na
  Knowledge Base do cliente
        │
        ▼
  Monta prompt para Claude API
  (system prompt + contexto + tools)
        │
        ▼
  Claude processa e pode chamar:
  ├── get_client_info()
  ├── check_availability(date, time)
  └── book_appointment(service, date, time)
        │
        ▼
  Resposta salva no histórico
        │
        ▼
  Retorna resposta ao cliente
```

---

## Estrutura de Pacotes (Spring Boot)

```
com.atendeai/
├── config/
│   ├── SecurityConfig.java
│   ├── ClaudeApiConfig.java
│   └── FlywayConfig.java
│
├── modules/
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── service/AuthService.java
│   │   ├── dto/LoginRequest.java
│   │   └── dto/TokenResponse.java
│   │
│   ├── customer/
│   │   ├── controller/CustomerController.java
│   │   ├── service/CustomerService.java
│   │   ├── repository/CustomerRepository.java
│   │   ├── model/Customer.java
│   │   └── dto/
│   │
│   ├── scheduling/
│   │   ├── controller/SchedulingController.java
│   │   ├── service/SchedulingService.java
│   │   ├── repository/AppointmentRepository.java
│   │   ├── model/Appointment.java
│   │   └── dto/
│   │
│   ├── chatbot/
│   │   ├── controller/ChatController.java
│   │   ├── service/ChatService.java
│   │   ├── service/ClaudeApiService.java
│   │   ├── service/ToolExecutorService.java
│   │   ├── model/Conversation.java
│   │   ├── model/Message.java
│   │   └── dto/
│   │
│   ├── knowledge/
│   │   ├── controller/KnowledgeController.java
│   │   ├── service/KnowledgeService.java
│   │   ├── service/EmbeddingService.java
│   │   ├── repository/KnowledgeDocumentRepository.java
│   │   ├── model/KnowledgeDocument.java
│   │   └── dto/
│   │
│   └── history/
│       ├── controller/HistoryController.java
│       ├── service/HistoryService.java
│       └── dto/
│
└── shared/
    ├── exception/GlobalExceptionHandler.java
    ├── exception/BusinessException.java
    ├── response/ApiResponse.java
    └── util/DateUtil.java
```

---

## Schema do Banco de Dados

### Tabelas principais

```sql
-- Usuários (admin, atendentes, clientes com acesso ao sistema)
users (id, email, password_hash, role, created_at)

-- Clientes (quem solicita atendimento)
customers (id, name, email, phone, address, notes, created_at)

-- Agendamentos
appointments (
  id, customer_id, service_type, title,
  scheduled_at, duration_minutes,
  status, notes, created_by, created_at, updated_at
)

-- Conversas do chatbot
conversations (id, customer_id, started_at, ended_at, summary)

-- Mensagens da conversa
messages (
  id, conversation_id, role, -- 'user' | 'assistant'
  content, tool_calls, created_at
)

-- Base de conhecimento por cliente
knowledge_documents (
  id, customer_id, -- null = base global
  title, content,
  embedding vector(1536), -- pgvector
  created_at
)

-- Histórico de serviços
service_history (
  id, customer_id, appointment_id,
  service_type, description,
  completed_at, attendant_notes
)
```

---

## Contratos de API (REST)

### Auth
```
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

### Clientes
```
GET    /api/v1/customers
POST   /api/v1/customers
GET    /api/v1/customers/{id}
PUT    /api/v1/customers/{id}
DELETE /api/v1/customers/{id}
GET    /api/v1/customers/{id}/history
```

### Agendamentos
```
GET  /api/v1/appointments?date=&customer_id=
POST /api/v1/appointments
GET  /api/v1/appointments/{id}
PUT  /api/v1/appointments/{id}
PUT  /api/v1/appointments/{id}/status
GET  /api/v1/appointments/schedule?from=&to=
GET  /api/v1/appointments/availability?date=&service=
```

### Chatbot
```
POST /api/v1/chat/start          — inicia conversa (retorna conversation_id)
POST /api/v1/chat/{id}/message   — envia mensagem, retorna resposta
GET  /api/v1/chat/{id}/messages  — histórico da conversa
POST /api/v1/chat/{id}/end       — encerra conversa
```

### Base de Conhecimento
```
GET    /api/v1/knowledge?customer_id=
POST   /api/v1/knowledge
DELETE /api/v1/knowledge/{id}
POST   /api/v1/knowledge/search   — busca semântica
```

---

## Variáveis de Ambiente

```env
# Banco
DB_URL=jdbc:postgresql://localhost:5432/atendeai
DB_USER=atendeai
DB_PASSWORD=secret

# Claude API
ANTHROPIC_API_KEY=sk-ant-...
CLAUDE_MODEL=claude-3-5-haiku-20241022

# JWT
JWT_SECRET=...
JWT_EXPIRATION_MS=86400000
```

---

## Docker Compose (dev)

```yaml
services:
  postgres:
    image: pgvector/pgvector:pg16
    environment:
      POSTGRES_DB: atendeai
      POSTGRES_USER: atendeai
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      - DB_URL=jdbc:postgresql://postgres:5432/atendeai
      - DB_USER=atendeai
      - DB_PASSWORD=secret
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}

volumes:
  postgres_data:
```

---

## Próximos Passos

1. **Setup do projeto** — `spring initializr` com dependências: Web, JPA, Security, Flyway, Lombok
2. **Docker Compose** — subir PostgreSQL com pgvector
3. **Migrations Flyway** — criar as tabelas
4. **Auth module** — JWT + Spring Security
5. **Customer module** — CRUD básico
6. **Chatbot module** — integração com Claude API + tool use
7. **Knowledge Base** — embeddings + pgvector search
8. **Scheduling module** — agendamentos + cronograma
9. **Frontend** — tela home + UI do chatbot (React ou Thymeleaf)
