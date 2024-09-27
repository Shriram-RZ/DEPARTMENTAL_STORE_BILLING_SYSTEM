CREATE DATABASE store_db;

USE store_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('Admin', 'Store Manager', 'Cashier') NOT NULL
);

CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL
);

USE store_db;

-- Inserting Admin User
INSERT INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'Admin');

CREATE INDEX idx_item_code ON inventory(item_code);

ALTER TABLE inventory MODIFY item_code VARCHAR(20);
ALTER TABLE sales MODIFY item_code VARCHAR(20);

ALTER TABLE sales
ADD CONSTRAINT fk_sales_inventory
FOREIGN KEY (item_code) REFERENCES inventory(item_code);
CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL
);

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    total DOUBLE NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_username VARCHAR(50) NOT NULL
);


