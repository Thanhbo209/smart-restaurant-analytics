-- orders
CREATE INDEX IF NOT EXISTS idx_orders_status_created_at
ON orders(status, created_at DESC)
WHERE status = 'COMPLETED';



CREATE INDEX IF NOT EXISTS idx_orders_type_created_at
ON orders(type, created_at DESC);

-- order_items
CREATE INDEX IF NOT EXISTS idx_order_items_order_id
ON order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_order_items_product_covering
ON order_items(product_id)
INCLUDE (quantity, subtotal, order_id);

-- products
CREATE INDEX IF NOT EXISTS idx_products_category_id_active
ON products(category_id)
WHERE is_active = true;

