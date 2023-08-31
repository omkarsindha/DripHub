-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 02, 2023 at 01:27 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `drip_hub`
--

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_name` varchar(50) NOT NULL,
  `item_owner` varchar(50) NOT NULL,
  `item_path` varchar(100) NOT NULL,
  `item_price` int(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_name`, `item_owner`, `item_path`, `item_price`) VALUES
('expensive', 'ramnik', '/posts/2805.2222793214455.jpeg', 200),
('hoodie  expensive', 'pranjal', '/posts/3561.825921265832.jpeg', 30),
('flag', 'pranjal', '/posts/5598.198955313746.jpeg', 10),
('phone', 'ramnik', '/posts/1210.048268679848.jpeg', 500);

-- --------------------------------------------------------

--
-- Table structure for table `sold`
--

CREATE TABLE `sold` (
  `item_name` varchar(50) NOT NULL,
  `item_owner` varchar(50) NOT NULL,
  `item_newowner` varchar(50) NOT NULL,
  `item_path` varchar(50) NOT NULL,
  `item_price` varchar(50) NOT NULL,
  `item_id` bigint(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sold`
--

INSERT INTO `sold` (`item_name`, `item_owner`, `item_newowner`, `item_path`, `item_price`, `item_id`) VALUES
('photo', 'ramnik', 'omkar', '/posts/5717.8834992871.jpeg', '123', 54926795),
('wallpaper', 'ramnik', 'omkar', '/posts/8440.554623645716.jpeg', '100', 991055),
('hoodie ', 'pranjal', 'omkar', '/posts/8776.846825639514.jpeg', '30', 2815535),
('flag', 'pranjal', 'omkar', '/posts/4408.678682162272.jpeg', '1000', 60325939),
('road', 'pranjal', 'omkar', '/posts/7573.6255261517745.jpeg', '10000', 98611106),
('phone', 'ramnik', 'omkar', '/posts/1210.048268679848.jpeg', '500', 96811896);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `name` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`name`, `username`, `password`, `email`, `type`) VALUES
('Jinal Sindha', 'jinal', 'jinal123', 'jinal@gmail.com', 'Seller'),
('Omkar Sindha', 'omkar', 'omkar123', 'omkar@gmail.com', 'Buyer'),
('Pranjal Chauhan', 'pranjal', 'pranjal123', 'prajal@gmail.com', 'Seller'),
('Ramnik Gill', 'ramnik', 'ramnik123', 'ramnik@gmail.com', 'Seller');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
