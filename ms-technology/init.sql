-- ============================================
-- Database Schema for Technology Microservice
-- ============================================
-- This script is executed automatically when the MySQL container starts
-- via docker-entrypoint-initdb.d

-- Create database (if not exists - Docker already creates it via env var)
CREATE DATABASE IF NOT EXISTS db_tecnology;
USE db_tecnology;

-- ============================================
-- TECHNOLOGIES Table
-- ============================================

CREATE TABLE IF NOT EXISTS technologies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
    
    -- Constraints based on HU-001
    CONSTRAINT uk_technologies_name UNIQUE (name),
    CONSTRAINT chk_technologies_name_length CHECK (CHAR_LENGTH(name) <= 50),
    CONSTRAINT chk_technologies_description_length CHECK (CHAR_LENGTH(description) <= 90),
    CONSTRAINT chk_technologies_name_not_empty CHECK (CHAR_LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_technologies_description_not_empty CHECK (CHAR_LENGTH(TRIM(description)) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Indexes for Performance
-- ============================================

-- Index for name lookups (already covered by unique constraint, but explicit)
CREATE INDEX idx_technologies_name ON technologies(name);

