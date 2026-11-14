package com.startup.validator.entity;

/**
 * Role enum representing the authorization levels of a user in our system.
 * 
 * We support:
 * 1. USER: Standard role for submitting startup ideas, editing/deleting them, and viewing AI analyses.
 * 2. ADMIN: Advanced role for system administration, managing users, and monitoring statistics.
 */
public enum Role {
    USER,
    ADMIN
}
