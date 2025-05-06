CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    merchant_codept_id VARCHAR(255) NOT NULL,
    warehouse_codept_id VARCHAR(255) NOT NULL,
    merchant_sku VARCHAR(255) NOT NULL,
    manufacturer_sku VARCHAR(255),
    manufacturer_name VARCHAR(255),
    ean VARCHAR(255),
    item_name VARCHAR(255)
);
