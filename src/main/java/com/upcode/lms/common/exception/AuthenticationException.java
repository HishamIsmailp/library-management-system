package com.upcode.lms.common.exception;

public class AuthenticationException extends RuntimeException {
    
    private final String errorCode;
    private final String module;
    
    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTH_ERROR";
        this.module = "AUTHENTICATION";
    }
    
    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.module = "AUTHENTICATION";
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUTH_ERROR";
        this.module = "AUTHENTICATION";
    }
    
    public AuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.module = "AUTHENTICATION";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getModule() {
        return module;
    }
    
    // Common authentication exceptions
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException(
            "Invalid username or password",
            "INVALID_CREDENTIALS"
        );
    }
    
    public static AuthenticationException userNotFound(String username) {
        return new AuthenticationException(
            String.format("User not found: %s", username),
            "USER_NOT_FOUND"
        );
    }
    
    public static AuthenticationException accountDisabled(String username) {
        return new AuthenticationException(
            String.format("Account is disabled: %s", username),
            "ACCOUNT_DISABLED"
        );
    }
    
    public static AuthenticationException accountLocked(String username) {
        return new AuthenticationException(
            String.format("Account is locked: %s", username),
            "ACCOUNT_LOCKED"
        );
    }
    
    public static AuthenticationException invalidToken() {
        return new AuthenticationException(
            "Invalid or expired token",
            "INVALID_TOKEN"
        );
    }
    
    public static AuthenticationException tokenExpired() {
        return new AuthenticationException(
            "Token has expired",
            "TOKEN_EXPIRED"
        );
    }
    
    public static AuthenticationException insufficientPermissions() {
        return new AuthenticationException(
            "Insufficient permissions to access this resource",
            "INSUFFICIENT_PERMISSIONS"
        );
    }
    
    public static AuthenticationException userAlreadyExists(String username) {
        return new AuthenticationException(
            String.format("User already exists: %s", username),
            "USER_ALREADY_EXISTS"
        );
    }
}