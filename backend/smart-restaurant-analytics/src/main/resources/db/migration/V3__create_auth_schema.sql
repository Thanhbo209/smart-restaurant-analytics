-- V7__create_auth_schema.sql

CREATE TABLE users (
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(100)  NOT NULL UNIQUE,
    password            VARCHAR(255)  NOT NULL,
    full_name           VARCHAR(200)  NOT NULL,
    email               VARCHAR(100)  UNIQUE,
    role                VARCHAR(20)   NOT NULL,
    enabled             BOOLEAN       NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_valid_role CHECK (role IN ('ADMIN','MANAGER','CASHIER','WAITER','KITCHEN'))
);

CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64)   NOT NULL UNIQUE,
    device_id   VARCHAR(100)  NOT NULL,
    expires_at  TIMESTAMP     NOT NULL,
    revoked     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id   ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_revoked   ON refresh_tokens(revoked) WHERE revoked = FALSE;

-- Seed ADMIN account (password: Admin@1234 — change immediately)
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System Admin', 'ADMIN');