-- RentWise Database Setup Script
-- This script runs automatically when MySQL container starts for the first time
-- MySQL will execute all .sql files in /docker-entrypoint-initdb.d/ directory

-- User Service Database
CREATE DATABASE IF NOT EXISTS rentwise_user_db;

-- Property Service Database
CREATE DATABASE IF NOT EXISTS rentwise_property_db;

-- Tenant Service Database
CREATE DATABASE IF NOT EXISTS rentwise_tenant_db;

-- Note: Tables will be created automatically by Hibernate
-- when services start with spring.jpa.hibernate.ddl-auto=update
-- Each service connects to its own database using the connection URL

-- ============================================
-- Property Service Database - Sample Data
-- ============================================
-- Create properties table and insert 3 sample apartments
USE rentwise_property_db;

-- Create properties table (matching Hibernate entity structure)
CREATE TABLE IF NOT EXISTS properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip_code VARCHAR(255),
    type VARCHAR(255),
    bedrooms INT,
    bathrooms INT,
    rent_amount DECIMAL(38, 2),
    status VARCHAR(255),
    user_id BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- Insert 3 sample apartment properties
-- Property 1: Modern Downtown Apartment
INSERT INTO properties (name, address, city, state, zip_code, type, bedrooms, bathrooms, rent_amount, status, user_id, created_at, updated_at)
VALUES (
    'Sunset View Apartments - Unit 301',
    '123 Main Street, Apt 301',
    'New York',
    'NY',
    '10001',
    'APARTMENT',
    2,
    2,
    2500.00,
    'AVAILABLE',
    1,
    NOW(),
    NOW()
);

-- Property 2: Cozy Studio Apartment
INSERT INTO properties (name, address, city, state, zip_code, type, bedrooms, bathrooms, rent_amount, status, user_id, created_at, updated_at)
VALUES (
    'Riverside Studio Apartment',
    '456 Oak Avenue, Unit 5B',
    'Los Angeles',
    'CA',
    '90001',
    'APARTMENT',
    1,
    1,
    1800.00,
    'AVAILABLE',
    1,
    NOW(),
    NOW()
);

-- Property 3: Spacious 3-Bedroom Apartment
INSERT INTO properties (name, address, city, state, zip_code, type, bedrooms, bathrooms, rent_amount, status, user_id, created_at, updated_at)
VALUES (
    'Park Plaza Luxury Apartments - Unit 1205',
    '789 Park Boulevard, Apt 1205',
    'Chicago',
    'IL',
    '60601',
    'APARTMENT',
    3,
    2,
    3200.00,
    'AVAILABLE',
    1,
    NOW(),
    NOW()
);
