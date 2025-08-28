CREATE table if not exists customer (
    id UUID PRIMARY KEY,
    login VARCHAR(20),
    email VARCHAR(20),
    is_active BOOLEAN
);
CREATE table if not exists product (
    id UUID PRIMARY KEY,
    name VARCHAR,
    article VARCHAR,
    dictionary VARCHAR,
    category VARCHAR,
    price NUMERIC(10, 2),
    qty NUMERIC(10, 2),
    inserted_at TIMESTAMP WITH TIME ZONE,
    last_qty_change TIMESTAMP WITH TIME ZONE,
    is_available BOOLEAN
);
CREATE table if not exists "order" (
    id UUID PRIMARY KEY,
    customer_id UUID,
    status VARCHAR(10),
    delivery_address VARCHAR(255)
);
CREATE table if not exists ordered_product (
    id UUID PRIMARY KEY,
    order_id UUID,
    product_id UUID,
    qty NUMERIC(10, 2),
    price NUMERIC(10, 2),
    UNIQUE  (order_id, product_id)
);
