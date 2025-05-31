package com.upcode.lms.auth.entity;

public enum Role {
    ADMIN("ADMIN", "System Administrator"),
    LIBRARIAN("LIBRARIAN", "Library Staff"),
    STUDENT("STUDENT", "Student User");
    
    private final String code;
    private final String description;
    
    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
    
    public static Role fromCode(String code) {
        for (Role role : Role.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }
    
    public boolean hasPermission(String permission) {
        return switch (this) {
            case ADMIN -> true; // Admin has all permissions
            case LIBRARIAN -> permission.startsWith("BOOK_") || 
                             permission.startsWith("TRANSACTION_") || 
                             permission.startsWith("FINE_") ||
                             permission.startsWith("REPORT_");
            case STUDENT -> permission.startsWith("BOOK_READ") || 
                           permission.startsWith("RESERVATION_") ||
                           permission.startsWith("PROFILE_");
        };
    }
}