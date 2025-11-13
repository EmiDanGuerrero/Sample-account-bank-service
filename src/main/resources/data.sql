-- =========================================================
--  init-db.sql  (H2)
--  Script de ejemplo para inicializar la base de datos
--  Tabla: bank_accounts
-- =========================================================

-- (Opcional) Borrar tabla si ya existe - útil en desarrollo
DROP TABLE IF EXISTS bank_accounts;

CREATE TABLE bank_accounts (
    id            UUID           PRIMARY KEY,
    account_number VARCHAR(50)   NOT NULL,
    cbu            VARCHAR(50)   NOT NULL,
    owner_name     VARCHAR(100)  NOT NULL,
    owner_document VARCHAR(50)   NOT NULL,
    currency       VARCHAR(10)   NOT NULL,
    balance        DECIMAL(19,2) NOT NULL,
    status         VARCHAR(20)   NOT NULL,
    branch_code    VARCHAR(20)   NOT NULL,
    created_at     TIMESTAMP     NOT NULL,
    updated_at     TIMESTAMP     NOT NULL,
    CONSTRAINT uk_bank_account_cbu UNIQUE (cbu)
);


INSERT INTO bank_accounts (
    id, account_number, cbu, owner_name, owner_document,
    currency, balance, status, branch_code, created_at, updated_at
) VALUES
  (
    '11111111-1111-1111-1111-111111111111',
    'ACC-001',
    '1230000100000000000011',
    'Juan Pérez',
    '30123456',
    'ARS',
    5000.00,
    'ACTIVE',
    '001',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
  ),
  (
    '22222222-2222-2222-2222-222222222222',
    'ACC-002',
    '1230000100000000000022',
    'María Gómez',
    '27999888',
    'USD',
    2500.50,
    'ACTIVE',
    '002',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
  ),
  (
    '33333333-3333-3333-3333-333333333333',
    'ACC-003',
    '1230000100000000000033',
    'Carlos López',
    '20111222',
    'ARS',
    100.00,
    'CLOSED',
    '003',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
  );
