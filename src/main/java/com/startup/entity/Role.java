package com.startup.entity;

/**
 * Role enumeration defining application authority levels.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public static Role fromString(String roleName) {
        if (roleName == null) return ROLE_USER;
        String clean = roleName.trim().toUpperCase();
        if (clean.equals("ADMIN") || clean.equals("ROLE_ADMIN")) {
            return ROLE_ADMIN;
        }
        return ROLE_USER;
    }
}
