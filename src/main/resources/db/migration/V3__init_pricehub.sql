-- =============================================
-- V3: Initial schema for PriceHub
-- Entities: retail_chains, stores, articles, price_entries
-- Created: 2025-05-16
-- =============================================

-- Retail Chains (no timestamps)
CREATE TABLE retail_chains (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL
);

-- Stores (with timestamps)
CREATE TABLE stores (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    store_code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    chain_id INT8 NOT NULL REFERENCES retail_chains(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Articles (with timestamps)
CREATE TABLE articles (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    product_code VARCHAR(255) NOT NULL,
    barcode VARCHAR(255),
    brand VARCHAR(255),
    unit VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT uq_articles_product_code UNIQUE (product_code)
);

-- Price Entries (with timestamps)
CREATE TABLE price_entries (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    store_id INT8 NOT NULL REFERENCES stores(id),
    article_id INT8 NOT NULL REFERENCES articles(id),
    price_date DATE NOT NULL,
    retail_price NUMERIC(10,2) NOT NULL,
    price_per_unit NUMERIC(10,2) NOT NULL,
    anchor_price NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT uq_price_entries_unique_entry UNIQUE (store_id, article_id, price_date)
);
