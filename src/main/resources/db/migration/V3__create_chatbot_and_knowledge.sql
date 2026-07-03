-- =====================
-- CONVERSATIONS (sessões do chatbot)
-- =====================
CREATE TABLE conversations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    channel     VARCHAR(50) NOT NULL DEFAULT 'WEB', -- WEB | WHATSAPP | etc
    summary     TEXT,
    started_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    ended_at    TIMESTAMP
);

CREATE INDEX idx_conversations_customer ON conversations(customer_id);

-- =====================
-- MESSAGES (mensagens da conversa)
-- =====================
CREATE TABLE messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role            VARCHAR(20) NOT NULL, -- 'user' | 'assistant' | 'tool'
    content         TEXT,
    tool_calls      JSONB,   -- tool use do Claude (function calls)
    tool_result     JSONB,   -- resultado das tools
    tokens_used     INTEGER,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_created      ON messages(created_at);

-- =====================
-- KNOWLEDGE BASE (base de conhecimento)
-- =====================
CREATE TABLE knowledge_documents (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID REFERENCES customers(id), -- NULL = base global
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    category    VARCHAR(100),
    -- Embedding gerado pela API de embeddings (dimensão 1536 para text-embedding-3-small)
    embedding   vector(1536),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_knowledge_customer ON knowledge_documents(customer_id);
-- Índice de busca vetorial (HNSW — mais rápido para buscas aproximadas)
CREATE INDEX idx_knowledge_embedding ON knowledge_documents
    USING hnsw (embedding vector_cosine_ops);
