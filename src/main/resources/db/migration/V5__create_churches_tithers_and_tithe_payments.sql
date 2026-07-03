-- =====================
-- CHURCHES (igrejas / congregações)
-- =====================
CREATE TABLE churches (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    address     TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_churches_name ON churches(name);

INSERT INTO churches (name) VALUES ('Igreja Sede'), ('Congregação Norte'), ('Congregação Sul');

-- =====================
-- TITHERS (dizimistas)
-- =====================
CREATE TABLE tithers (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(255) NOT NULL,
    cpf               VARCHAR(14) NOT NULL,
    phone             VARCHAR(50),
    email             VARCHAR(255),
    birth_date        DATE,
    zip_code          VARCHAR(10),
    street            VARCHAR(255),
    number            VARCHAR(20),
    neighborhood      VARCHAR(120),
    city              VARCHAR(120),
    state             VARCHAR(2),
    church_id         UUID NOT NULL REFERENCES churches(id),
    start_date        DATE,
    reference_amount  NUMERIC(10,2),
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_tithers_cpf ON tithers(cpf);
CREATE INDEX idx_tithers_church ON tithers(church_id);
CREATE INDEX idx_tithers_name ON tithers(name);

-- =====================
-- TITHE_PAYMENTS (pagamentos mensais de dízimo)
-- =====================
CREATE TABLE tithe_payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tither_id        UUID NOT NULL REFERENCES tithers(id),
    reference_month  DATE NOT NULL, -- sempre o dia 1 do mês de competência
    amount           NUMERIC(10,2) NOT NULL,
    paid_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    notes            TEXT,
    registered_by    UUID REFERENCES users(id),
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_tithe_payments_tither_month ON tithe_payments(tither_id, reference_month);
CREATE INDEX idx_tithe_payments_month ON tithe_payments(reference_month);
