CREATE TABLE products (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    merchant_codept_id VARCHAR(255) NOT NULL,
    warehouse_codept_id VARCHAR(255) NOT NULL,
    merchant_sku VARCHAR(255) NOT NULL,
    manufacturer_sku VARCHAR(255),
    manufacturer_name VARCHAR(255),
    ean VARCHAR(255),
    item_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
