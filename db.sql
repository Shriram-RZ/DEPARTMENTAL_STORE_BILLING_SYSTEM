-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS store_db;

-- Use the database
USE store_db;

-- Create 'users' table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('Admin', 'Store Manager', 'Cashier') NOT NULL
    profile_photo_path VARCHAR(255) DEFAULT 'path/to/default/photo.jpg'
);

-- Create 'inventory' table if it doesn't exist
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL
);

-- Insert Admin User only if not already present
INSERT INTO users (username, password, role)
SELECT 'admin', 'admin123', 'Admin'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

-- Create index for 'item_code' in 'inventory' table if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_item_code ON inventory(item_code);

-- Modify the 'item_code' column in 'inventory' table
ALTER TABLE inventory MODIFY item_code VARCHAR(20);

-- Create 'sales' table if it doesn't exist
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    total DOUBLE NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_username VARCHAR(50) NOT NULL
);

-- Modify the 'item_code' column in 'sales' table
ALTER TABLE sales MODIFY item_code VARCHAR(20);

-- Add a foreign key to link 'sales' table with 'inventory' table
ALTER TABLE sales
ADD CONSTRAINT fk_sales_inventory
FOREIGN KEY (item_code) REFERENCES inventory(item_code);
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(20) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    total DOUBLE NOT NULL,
    invoice_number VARCHAR(50),  -- Changed from INT to VARCHAR
    sale_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_username VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(20),
    FOREIGN KEY (item_code) REFERENCES inventory(item_code)
);



