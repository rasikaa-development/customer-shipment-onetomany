-- Database is created automatically (createDatabaseIfNotExist=true) and the tables by
-- Hibernate (ddl-auto=update). The DDL below is what Hibernate generates, for reference.
CREATE DATABASE IF NOT EXISTS shipmentdb;
USE shipmentdb;

CREATE TABLE IF NOT EXISTS customers (
  customer_id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL,
  email         VARCHAR(150) NOT NULL,
  CONSTRAINT uk_customers_email UNIQUE (email)
);

-- One-to-Many: many shipments rows point to one customers row.
-- customer_id is NOT NULL, so every shipment belongs to exactly one customer.
CREATE TABLE IF NOT EXISTS shipments (
  shipment_id BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  source      VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  status      ENUM('CREATED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL,
  customer_id BIGINT       NOT NULL,
  CONSTRAINT fk_shipments_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);
