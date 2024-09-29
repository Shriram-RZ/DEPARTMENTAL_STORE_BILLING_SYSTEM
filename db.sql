-- Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS store_db;

-- Select the database
USE store_db;

-- Create the `inventory` table
CREATE TABLE `inventory` (
  `id` int(11) NOT NULL,
  `item_code` varchar(20) DEFAULT NULL,
  `item_name` varchar(100) NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insert data into `inventory` table
INSERT INTO `inventory` (`id`, `item_code`, `item_name`, `price`, `quantity`) VALUES
(1, '1234567890', 'apple', 10, 98),
(4, '1234567890', '', 10, 98);

-- Create the `sales` table
CREATE TABLE `sales` (
  `id` int(11) NOT NULL,
  `item_code` varchar(20) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  `total` double NOT NULL,
  `invoice_number` varchar(50) DEFAULT NULL,
  `sale_datetime` timestamp NOT NULL DEFAULT current_timestamp(),
  `cashier_username` varchar(50) NOT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `customer_phone` varchar(20) DEFAULT NULL,
  `total_cost` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insert data into `sales` table
INSERT INTO `sales` (`id`, `item_code`, `item_name`, `quantity`, `total`, `invoice_number`, `sale_datetime`, `cashier_username`, `customer_name`, `customer_phone`, `total_cost`) VALUES
(1, '1234567890', 'apple', 1, 100, 'INV-20240929154214', '2024-09-29 10:12:14', 'cashier', 'asdf', '1234567890', NULL),
(2, '1234567890', 'apple', 2, 200, 'INV-20240929154214', '2024-09-29 10:12:14', 'cashier', 'asdf', '1234567890', NULL),
(3, '1234567890', 'apple', 1, 100, 'INV-20240929154501', '2024-09-29 10:15:01', 'cashier', 'asdf', '1234567890', NULL),
(4, '1234567890', 'apple', 2, 200, 'INV-20240929154501', '2024-09-29 10:15:01', 'cashier', 'asdf', '1234567890', NULL),
(5, '1234567890', 'apple', 1, 100, 'INV-20240929155133', '2024-09-29 10:21:33', 'cashier', 'asdf', '099372829303', NULL),
(6, '1234567890', 'apple', 2, 200, 'INV-20240929155133', '2024-09-29 10:21:33', 'cashier', 'asdf', '099372829303', NULL),
(7, '1234567890', 'apple', 1, 100, 'INV-20240929160412', '2024-09-29 10:34:12', 'cashier', 'asdf', '13434232234', NULL),
(8, '1234567890', 'apple', 1, 100, 'INV-20240929160412', '2024-09-29 10:34:12', 'cashier', 'asdf', '13434232234', NULL),
(11, '1234567890', 'apple', 1, 100, 'INV-1727607787613', '2024-09-29 11:03:09', 'cashier', 'dfojsjod', '31232424342', NULL),
(12, '1234567890', 'apple', 1, 100, 'INV-1727608174023', '2024-09-29 11:09:35', 'cashier', 'pwowwpw', '124333232433', NULL),
(13, '1234567890', 'apple', 1, 10, 'INV-1727614193071', '2024-09-29 12:49:54', 'cashier', 'edweww', '12332234242', NULL),
(14, '1234567890', 'apple', 1, 10, 'INV-1727615498871', '2024-09-29 13:11:40', 'cashier', 'sjiwds', '12343546475745', NULL);

-- Create the `users` table
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` enum('Admin','Store Manager','Cashier') NOT NULL,
  `profile_photo_path` varchar(255) DEFAULT 'path/to/default/photo.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insert data into `users` table
INSERT INTO `users` (`id`, `username`, `password`, `role`, `profile_photo_path`) VALUES
(1, 'admin', 'admin123', 'Admin', 'path/to/default/photo.jpg'),
(2, 'manager', 'manager123', 'Store Manager', 'path/to/default/photo.jpg'),
(3, 'cashier', 'cashier123', 'Cashier', 'path/to/default/photo.jpg');

-- Add primary keys and indexes

-- For `inventory` table
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_item_code` (`item_code`);

-- For `sales` table
ALTER TABLE `sales`
  ADD PRIMARY KEY (`id`),
  ADD KEY `item_code` (`item_code`);

-- For `users` table
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

-- Set auto-increment values for the tables
ALTER TABLE `inventory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

ALTER TABLE `sales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

-- Add foreign key constraint for the `sales` table
ALTER TABLE `sales`
  ADD CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`item_code`) REFERENCES `inventory` (`item_code`);

-- Commit the transaction
COMMIT;
