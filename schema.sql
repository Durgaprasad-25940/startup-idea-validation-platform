-- =============================================================================
-- STARTUP IDEA VALIDATION PLATFORM
-- Database Schema Definition (MySQL)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS startup_idea_db;
USE startup_idea_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Startup Ideas Table
CREATE TABLE IF NOT EXISTS startup_ideas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    industry VARCHAR(100) NOT NULL,
    target_audience VARCHAR(255) NOT NULL,
    stage VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ideas_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Analysis Reports Table
CREATE TABLE IF NOT EXISTS analysis_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idea_id BIGINT NOT NULL UNIQUE,
    market_analysis MEDIUMTEXT NOT NULL,
    competitor_analysis MEDIUMTEXT NOT NULL,
    swot_strengths MEDIUMTEXT NOT NULL,
    swot_weaknesses MEDIUMTEXT NOT NULL,
    swot_opportunities MEDIUMTEXT NOT NULL,
    swot_threats MEDIUMTEXT NOT NULL,
    improvement_suggestions MEDIUMTEXT NOT NULL,
    viability_score INT DEFAULT 75,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_idea FOREIGN KEY (idea_id) REFERENCES startup_ideas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for performance
CREATE INDEX idx_startup_ideas_user ON startup_ideas(user_id);
CREATE INDEX idx_startup_ideas_industry ON startup_ideas(industry);
CREATE INDEX idx_analysis_reports_idea ON analysis_reports(idea_id);
