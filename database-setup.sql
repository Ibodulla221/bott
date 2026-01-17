-- PostgreSQL database setup for Kupon Bot

-- Create database
CREATE DATABASE kuponbot;

-- Create user (optional, if you want separate user)
-- CREATE USER kuponbot_user WITH PASSWORD 'your_password';
-- GRANT ALL PRIVILEGES ON DATABASE kuponbot TO kuponbot_user;

-- Connect to kuponbot database and create tables (Spring Boot will handle this automatically)
-- But you can run this manually if needed:

\c kuponbot;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    state VARCHAR(20) DEFAULT 'START',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Coupons table
CREATE TABLE IF NOT EXISTS coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(5) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_telegram_id ON users(telegram_id);
CREATE INDEX IF NOT EXISTS idx_coupons_code ON coupons(code);
CREATE INDEX IF NOT EXISTS idx_coupons_user_id ON coupons(user_id);
CREATE INDEX IF NOT EXISTS idx_coupons_status ON coupons(status);

-- Insert sample admin data (optional)
-- INSERT INTO users (telegram_id, phone_number, first_name, last_name, state) 
-- VALUES (123456789, '+998901234567', 'Admin', 'User', 'REGISTERED')
-- ON CONFLICT (telegram_id) DO NOTHING;