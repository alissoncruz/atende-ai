-- =====================
-- APPOINTMENTS (agendamentos)
-- =====================
CREATE TABLE appointments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id      UUID NOT NULL REFERENCES customers(id),
    created_by       UUID NOT NULL REFERENCES users(id),
    service_type     VARCHAR(100) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    scheduled_at     TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 60,
    status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- PENDING | CONFIRMED | IN_PROGRESS | COMPLETED | CANCELLED
    notes            TEXT,
    cancelled_reason TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_customer    ON appointments(customer_id);
CREATE INDEX idx_appointments_scheduled   ON appointments(scheduled_at);
CREATE INDEX idx_appointments_status      ON appointments(status);

-- =====================
-- SERVICE HISTORY (histórico de serviços)
-- =====================
CREATE TABLE service_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID NOT NULL REFERENCES customers(id),
    appointment_id  UUID REFERENCES appointments(id),
    service_type    VARCHAR(100) NOT NULL,
    description     TEXT,
    attendant_notes TEXT,
    attendant_id    UUID REFERENCES users(id),
    completed_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_service_history_customer ON service_history(customer_id);
CREATE INDEX idx_service_history_completed ON service_history(completed_at);
