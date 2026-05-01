CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL,
    slug        VARCHAR(150)  NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    parent_id   BIGINT        REFERENCES categories(id),
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP     NOT NULL
);

CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200)   NOT NULL,
    slug            VARCHAR(200)   NOT NULL,
    price           NUMERIC(12,2)  NOT NULL CHECK (price >= 0),
    cost            NUMERIC(12,2)  CHECK (cost IS NULL OR cost >= 0),
    description     TEXT,
    sku             VARCHAR(100)   UNIQUE,
    image_url       VARCHAR(500),
    image_public_id VARCHAR(200),
    is_active       BOOLEAN        NOT NULL DEFAULT TRUE,
    is_available    BOOLEAN        NOT NULL DEFAULT TRUE,
    category_id     BIGINT         NOT NULL REFERENCES categories(id),
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP      NOT NULL
);