-- Admin padrão para desenvolvimento
-- Senha: admin123 (bcrypt)
INSERT INTO users (id, email, password, name, role)
VALUES (
    gen_random_uuid(),
    'admin@atendeai.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Administrador',
    'ADMIN'
);
