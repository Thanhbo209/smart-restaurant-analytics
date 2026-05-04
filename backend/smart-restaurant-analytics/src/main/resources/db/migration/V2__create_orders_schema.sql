CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    type             VARCHAR(20)    NOT NULL,
    channel          VARCHAR(20)    NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    payment_status   VARCHAR(20)    NOT NULL DEFAULT 'UNPAID',
    table_number     VARCHAR(20),
    customer_name    VARCHAR(200),
    phone            VARCHAR(30),
    address          TEXT,
    total_amount     NUMERIC(12,2)  NOT NULL DEFAULT 0,
    discount_amount  NUMERIC(12,2)  NOT NULL DEFAULT 0,
    final_amount     NUMERIC(12,2)  NOT NULL DEFAULT 0,
    notes            TEXT,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW(),

    -- Type constraints
    CONSTRAINT chk_dine_in_table
        CHECK (type != 'DINE_IN' OR table_number IS NOT NULL),
    CONSTRAINT chk_delivery_address
        CHECK (type != 'DELIVERY' OR address IS NOT NULL),
    CONSTRAINT chk_dine_in_no_address
        CHECK (type != 'DINE_IN' OR address IS NULL),

    -- Status constraints
    CONSTRAINT chk_valid_status
        CHECK (status IN ('PENDING','CONFIRMED','PREPARING','READY', 'OUT_FOR_DELIVERY',
                          'SERVED','DELIVERED','COMPLETED','CANCELLED')),
    CONSTRAINT chk_valid_type
        CHECK (type IN ('DINE_IN','TAKEAWAY','DELIVERY')),
    CONSTRAINT chk_valid_channel
        CHECK (channel IN ('IN_STORE','SELF_SERVICE','ONLINE')),
    CONSTRAINT chk_valid_payment_status
        CHECK (payment_status IN ('UNPAID','PAID','PARTIALLY_PAID','REFUNDED')),

   -- Enforce full type/status compatibility
   CONSTRAINT chk_type_status_compatibility CHECK (
       (type = 'DINE_IN' AND status IN ('PENDING','CONFIRMED','PREPARING','READY','SERVED','COMPLETED','CANCELLED')) OR
       (type = 'DELIVERY' AND status IN ('PENDING','CONFIRMED','PREPARING','READY','OUT_FOR_DELIVERY','DELIVERED','COMPLETED','CANCELLED')) OR
       (type = 'TAKEAWAY' AND status IN ('PENDING','CONFIRMED','PREPARING','READY','COMPLETED','CANCELLED'))
   ),
    -- Amounts must be non-negative
    CONSTRAINT chk_total_amount    CHECK (total_amount >= 0),
    CONSTRAINT chk_discount_amount CHECK (discount_amount >= 0),
    CONSTRAINT chk_final_amount    CHECK (final_amount >= 0)
);

CREATE TABLE order_items (
    id            BIGSERIAL PRIMARY KEY,
    order_id      BIGINT         NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id    BIGINT         NOT NULL,
    product_name  VARCHAR(200)   NOT NULL,
    price         NUMERIC(12,2)  NOT NULL CHECK (price >= 0),
    quantity      INTEGER        NOT NULL CHECK (quantity > 0),
    subtotal      NUMERIC(12,2)  NOT NULL CHECK (subtotal >= 0),
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE payments (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT         NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    method      VARCHAR(20)    NOT NULL,
    amount      NUMERIC(12,2)  NOT NULL CHECK (amount > 0),
    status      VARCHAR(20)    NOT NULL,
    paid_at     TIMESTAMP      NOT NULL DEFAULT NOW(),
    note        VARCHAR(500),

    CONSTRAINT chk_valid_payment_method
        CHECK (method IN ('CASH','CARD','MOMO','VNPAY','ZALOPAY','BANK_TRANSFER')),
    CONSTRAINT chk_valid_payment_result
        CHECK (status IN ('SUCCESS','FAILED','REFUNDED'))
);

-- V2__add_order_indexes.sql
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_type       ON orders(type);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);

-- Analytics: most common filter combo
CREATE INDEX idx_orders_status_type_date
    ON orders(status, type, created_at DESC);

CREATE INDEX idx_order_items_order_id   ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_payments_order_id      ON payments(order_id);