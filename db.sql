CREATE DATABASE departmental_store;

USE departmental_store;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    role ENUM('admin', 'storemanager', 'cashier')
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    price DECIMAL(10,2),
    quantity INT,
    min_quantity INT DEFAULT 25
);

CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    quantity INT,
    total DECIMAL(10,2),
    date DATE,
    time TIME
);

CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    report_type ENUM('daily', 'weekly', 'monthly'),
    data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
