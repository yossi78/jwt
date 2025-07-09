-- Initialize database
USE jwt_user_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT,
    birthday DATE
);

-- Create login table
CREATE TABLE IF NOT EXISTS login (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert sample data
INSERT INTO users (first_name, last_name, age, birthday) VALUES
('John', 'Doe', 30, '1993-05-15'),
('Jane', 'Smith', 25, '1998-08-22'),
('Mike', 'Johnson', 35, '1988-12-10');

-- Insert sample login credentials (password: password123)
-- Using correct BCrypt hash for "password123"
INSERT INTO login (username, password, user_id) VALUES
('john.doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 1),
('jane.smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 2),
('mike.johnson', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 3); 