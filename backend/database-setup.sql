-- RentWise Database Setup Script
-- Run this script in MySQL to create all required databases

-- User Service Database
CREATE DATABASE IF NOT EXISTS rentwise_user_db;
USE rentwise_user_db;

-- Property Service Database
CREATE DATABASE IF NOT EXISTS rentwise_property_db;
USE rentwise_property_db;

-- Tenant Service Database
CREATE DATABASE IF NOT EXISTS rentwise_tenant_db;
USE rentwise_tenant_db;

-- Note: Tables will be created automatically by Hibernate
-- when you run the services with spring.jpa.hibernate.ddl-auto=update

