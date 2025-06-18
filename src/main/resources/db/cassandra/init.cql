-- Cassandra initialization script

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS productdb 
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE productdb;

-- Create tables
CREATE TABLE IF NOT EXISTS products (
    id uuid PRIMARY KEY,
    name text,
    description text,
    price decimal,
    category text,
    stock int,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    username text,
    email text,
    password_hash text,
    first_name text,
    last_name text,
    is_active boolean,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS orders (
    id uuid PRIMARY KEY,
    user_id uuid,
    status text,
    total_amount decimal,
    items list<frozen<order_item>>,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id uuid,
    product_id uuid,
    quantity int,
    price decimal,
    PRIMARY KEY (order_id, product_id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id uuid PRIMARY KEY,
    user_id uuid,
    action text,
    resource_type text,
    resource_id uuid,
    details text,
    timestamp timestamp
);

CREATE TABLE IF NOT EXISTS categories (
    id uuid PRIMARY KEY,
    name text,
    description text,
    created_at timestamp,
    updated_at timestamp
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS ON products (category);
CREATE INDEX IF NOT EXISTS ON products (name);
CREATE INDEX IF NOT EXISTS ON users (email);
CREATE INDEX IF NOT EXISTS ON users (username);
CREATE INDEX IF NOT EXISTS ON orders (user_id);
CREATE INDEX IF NOT EXISTS ON orders (status);
CREATE INDEX IF NOT EXISTS ON audit_logs (user_id);
CREATE INDEX IF NOT EXISTS ON audit_logs (action);

-- Insert sample data
INSERT INTO categories (id, name, description, created_at, updated_at) VALUES 
(uuid(), 'Electronics', 'Electronic devices and gadgets', toTimestamp(now()), toTimestamp(now()));

INSERT INTO categories (id, name, description, created_at, updated_at) VALUES 
(uuid(), 'Clothing', 'Fashion and apparel', toTimestamp(now()), toTimestamp(now()));

INSERT INTO categories (id, name, description, created_at, updated_at) VALUES 
(uuid(), 'Books', 'Books and publications', toTimestamp(now()), toTimestamp(now()));

INSERT INTO products (id, name, description, price, category, stock, created_at, updated_at) VALUES 
(uuid(), 'iPhone 15 Pro', 'Latest iPhone with advanced features', 999.99, 'Electronics', 50, toTimestamp(now()), toTimestamp(now()));

INSERT INTO products (id, name, description, price, category, stock, created_at, updated_at) VALUES 
(uuid(), 'MacBook Pro M3', 'Powerful laptop for professionals', 1999.99, 'Electronics', 25, toTimestamp(now()), toTimestamp(now()));

INSERT INTO products (id, name, description, price, category, stock, created_at, updated_at) VALUES 
(uuid(), 'Nike Air Max', 'Comfortable running shoes', 129.99, 'Clothing', 100, toTimestamp(now()), toTimestamp(now())); 