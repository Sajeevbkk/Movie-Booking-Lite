CREATE DATABASE IF NOT EXISTS movie_booking_db;
USE movie_booking_db;

-- Temporarily disable foreign key checks to safely drop existing tables
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS theaters;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS shows;
DROP TABLE IF EXISTS movies;

SET FOREIGN_KEY_CHECKS = 1;

-- Table: movies
CREATE TABLE movies (
    movie_id         INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(100) NOT NULL,
    genre            VARCHAR(50),
    duration_minutes INT,
    ticket_price     DOUBLE NOT NULL
);

-- Table: shows
CREATE TABLE shows (
    show_id   INT AUTO_INCREMENT PRIMARY KEY,
    movie_id  INT NOT NULL,
    show_time VARCHAR(20) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);

-- Table: customers
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

-- Table: bookings
CREATE TABLE bookings (
    booking_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id    INT NOT NULL,
    show_id        INT NOT NULL,
    seats          VARCHAR(50) NOT NULL,
    num_tickets    INT NOT NULL,
    total_amount   DOUBLE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE
);

-- ==========================================
-- Fake / Sample Data for Testing & Demo
-- ==========================================

-- 1. Movies (Original syllabus movies + additional movies)
INSERT INTO movies (title, genre, duration_minutes, ticket_price) VALUES
('Space Odyssey', 'Sci-Fi', 140, 200.0),
('Laugh Out Loud', 'Comedy', 110, 150.0),
('The Last Detective', 'Thriller', 125, 180.0),
('Wonder Kids', 'Animation', 95, 120.0),
('Interstellar', 'Sci-Fi', 169, 250.0),
('Inception', 'Action/Sci-Fi', 148, 220.0),
('The Dark Knight', 'Action', 152, 200.0),
('Avatar: The Way of Water', 'Adventure', 192, 280.0);

-- 2. Shows for each movie (3 show times per movie)
INSERT INTO shows (movie_id, show_time) VALUES
(1, '10:00 AM'), (1, '02:00 PM'), (1, '07:00 PM'),
(2, '11:00 AM'), (2, '03:00 PM'), (2, '08:00 PM'),
(3, '12:00 PM'), (3, '04:00 PM'), (3, '09:00 PM'),
(4, '10:30 AM'), (4, '01:30 PM'), (4, '05:00 PM'),
(5, '10:00 AM'), (5, '02:30 PM'), (5, '07:30 PM'),
(6, '11:00 AM'), (6, '03:30 PM'), (6, '08:00 PM'),
(7, '12:00 PM'), (7, '04:30 PM'), (7, '09:00 PM'),
(8, '10:30 AM'), (8, '03:00 PM'), (8, '07:30 PM');

-- 3. Customers
INSERT INTO customers (name) VALUES
('Anu'),
('Rahul'),
('Priya Patel'),
('Amit Kumar'),
('Sneha Reddy'),
('Vikram Singh'),
('Ananya Iyer'),
('Rohan Verma');

-- 4. Bookings (sample test bookings + rich fake data)
INSERT INTO bookings (customer_id, show_id, seats, num_tickets, total_amount, payment_method, status) VALUES
(1, 1, 'A1,A2', 2, 400.00, 'Cash', 'BOOKED'),
(2, 4, 'B3', 1, 153.00, 'Card', 'BOOKED'),
(3, 1, 'B1,B2', 2, 408.00, 'Card', 'BOOKED'),
(4, 2, 'A3,A4', 2, 408.00, 'Card', 'BOOKED'),
(5, 7, 'A1', 1, 180.00, 'Cash', 'BOOKED'),
(6, 13, 'B3', 1, 255.00, 'Card', 'BOOKED'),
(7, 16, 'A1,A2', 2, 440.00, 'Cash', 'BOOKED'),
(8, 19, 'A4', 1, 200.00, 'Cash', 'CANCELLED'),
(3, 10, 'B4', 1, 120.00, 'Cash', 'CANCELLED'),
(2, 22, 'A1,A2', 2, 571.20, 'Card', 'BOOKED');
