-- Shopping Cart Database Schema for PostgreSQL
-- Version: 3.0
-- This is a complete base script to create the schema from scratch.

-- Drop all tables if they exist (for clean setup)
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS discount_rule_users CASCADE;
DROP TABLE IF EXISTS discount_rule_products CASCADE;
DROP TABLE IF EXISTS discount_rule_regions CASCADE;
DROP TABLE IF EXISTS discount_rule_currencies CASCADE;
DROP TABLE IF EXISTS discount_rule_categories CASCADE;
DROP TABLE IF EXISTS discount_conditions CASCADE;
DROP TABLE IF EXISTS discount_actions CASCADE;
DROP TABLE IF EXISTS discount_rules CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS shopping_carts CASCADE;
DROP TABLE IF EXISTS product_inventories CASCADE;
DROP TABLE IF EXISTS product_prices CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- #################################################################
-- ###################### TABLE CREATION ###########################
-- #################################################################

CREATE TABLE product_categories (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    parent_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    user_currency VARCHAR(3) NOT NULL DEFAULT 'GBP',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE products (
    product_id VARCHAR(8) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    status VARCHAR(50),
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE product_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(8) NOT NULL,
    region VARCHAR(10) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    price_in_smallest_unit INTEGER NOT NULL CHECK (price_in_smallest_unit > 0),
    effective_from_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE (product_id, region, currency, effective_from_utc)
);

CREATE TABLE product_inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(8) NOT NULL,
    region VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    version BIGINT NOT NULL DEFAULT 1,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE (product_id, region)
);

-- Shopping Carts table
CREATE TABLE shopping_carts (
    cart_id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Cart Items table
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(8) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    added_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES shopping_carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE (cart_id, product_id)
);

-- Discount Rules table
CREATE TABLE discount_rules (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    valid_from_utc TIMESTAMP WITH TIME ZONE,
    valid_until_utc TIMESTAMP WITH TIME ZONE,
    priority INTEGER NOT NULL DEFAULT 1,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE discount_conditions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    condition_type VARCHAR(30) NOT NULL, -- MIN_CART_SPEND, PRODUCT_IN_CART
    product_id VARCHAR(8),
    quantity INTEGER,
    amount_in_smallest_unit INTEGER,
    FOREIGN KEY (rule_id) REFERENCES discount_rules(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE discount_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    action_type VARCHAR(30) NOT NULL, -- PERCENTAGE_OFF, FIXED_AMOUNT_OFF, FREE_PRODUCT
    target_product_id VARCHAR(8),
    value DECIMAL(10,2),
    get_quantity INTEGER,
    FOREIGN KEY (rule_id) REFERENCES discount_rules(id) ON DELETE CASCADE,
    FOREIGN KEY (target_product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_status VARCHAR(20) NOT NULL, -- COMPLETED, FAILED, PENDING
    subtotal_in_smallest_unit INT NOT NULL,
    discount_in_smallest_unit INT NOT NULL,
    shipping_in_smallest_unit INT NOT NULL,
    total_in_smallest_unit INT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id VARCHAR(8) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price_in_smallest_unit INTEGER NOT NULL,
    total_price_in_smallest_unit INTEGER NOT NULL,
    discount_in_smallest_unit INTEGER NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);


CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id VARCHAR(100) NOT NULL,
    changed_at_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100),
    old_values JSONB,
    new_values JSONB
);

-- #################################################################
-- ###################### DATA INSERTION ###########################
-- #################################################################

INSERT INTO product_categories (id, name) VALUES (1, 'FRUITS'), (2, 'VEGETABLES');

INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, is_active) VALUES (1, 'admin', 'admin@shoppingcart.com', '$2a$10$rRgQ8ZXB.ZxPKXB4rJ1zKe4H8QZr8yJqE3u9o6K2L0nF1vL3qP7GW', 'Admin', 'User', 'ADMIN', TRUE);

INSERT INTO products (product_id, name, category_id, sku, description) VALUES
('APPLE', 'Apple', 1, 'FRUIT-001', 'Fresh red apples'),
('BANANA', 'Banana', 1, 'FRUIT-002', 'Ripe yellow bananas'),
('MELON', 'Melon', 1, 'FRUIT-003', 'Sweet melons'),
('LIME', 'Lime', 1, 'FRUIT-004', 'Fresh limes');

INSERT INTO product_prices (product_id, region, currency, price_in_smallest_unit) VALUES
('APPLE', 'UK', 'GBP', 35),
('BANANA', 'UK', 'GBP', 20),
('MELON', 'UK', 'GBP', 50),
('LIME', 'UK', 'GBP', 15),
('APPLE', 'US', 'USD', 45),
('BANANA', 'US', 'USD', 25),
('MELON', 'US', 'USD', 65),
('LIME', 'US', 'USD', 20);

INSERT INTO product_inventories (product_id, region, quantity) VALUES
('APPLE', 'UK', 100),
('BANANA', 'UK', 150),
('MELON', 'UK', 75),
('LIME', 'UK', 200);

INSERT INTO discount_rules (id, name, description, is_active) VALUES 
(1, 'Melon BOGO', 'Buy one get one free on melons', TRUE),
(2, 'Lime 3-for-2', 'Three limes for the price of two', TRUE),
(3, 'Melon-Lime Combo', 'Buy 2 Melons, get 1 Lime 50% off', TRUE);

-- Actions & Conditions for Rule 1 (Melon BOGO)
INSERT INTO discount_conditions (rule_id, condition_type, product_id, quantity) VALUES (1, 'PRODUCT_IN_CART', 'MELON', 1);
INSERT INTO discount_actions (rule_id, action_type, target_product_id, get_quantity) VALUES (1, 'FREE_PRODUCT', 'MELON', 1);

-- Actions & Conditions for Rule 2 (Lime 3-for-2)
INSERT INTO discount_conditions (rule_id, condition_type, product_id, quantity) VALUES (2, 'PRODUCT_IN_CART', 'LIME', 2);
INSERT INTO discount_actions (rule_id, action_type, target_product_id, get_quantity) VALUES (2, 'FREE_PRODUCT', 'LIME', 1);

-- Actions & Conditions for Rule 3 (Melon-Lime Combo)
INSERT INTO discount_conditions (rule_id, condition_type, product_id, quantity) VALUES (3, 'PRODUCT_IN_CART', 'MELON', 2);
INSERT INTO discount_actions (rule_id, action_type, target_product_id, value) VALUES (3, 'PERCENTAGE_OFF', 'LIME', 50.0);

-- #################################################################
-- ###################### INDEXES ##################################
-- #################################################################

CREATE INDEX idx_users_login ON users(username, is_deleted, is_active) WHERE is_deleted = FALSE AND is_active = TRUE;
CREATE INDEX idx_products_active ON products(product_id, is_deleted) WHERE is_deleted = FALSE;
CREATE INDEX idx_products_category_id ON products(category_id, is_deleted) WHERE is_deleted = FALSE;
CREATE INDEX idx_shopping_carts_active ON shopping_carts(user_id, status, is_deleted) WHERE is_deleted = FALSE AND status = 'ACTIVE';
CREATE INDEX idx_discounts_active ON discount_rules(is_active, is_deleted, valid_from_utc, valid_until_utc) WHERE is_deleted = FALSE AND is_active = TRUE;
CREATE INDEX idx_discount_conditions_rule ON discount_conditions(rule_id);
CREATE INDEX idx_discount_actions_rule ON discount_actions(rule_id);
CREATE INDEX idx_dcp_product_id ON discount_conditions(product_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);