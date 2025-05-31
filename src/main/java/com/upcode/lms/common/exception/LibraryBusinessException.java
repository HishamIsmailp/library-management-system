package com.upcode.lms.common.exception;

public class LibraryBusinessException extends RuntimeException {
    
    private final String errorCode;
    private final String module;
    
    public LibraryBusinessException(String message) {
        super(message);
        this.errorCode = "LMS_ERROR";
        this.module = "GENERAL";
    }
    
    public LibraryBusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.module = "GENERAL";
    }
    
    public LibraryBusinessException(String message, String errorCode, String module) {
        super(message);
        this.errorCode = errorCode;
        this.module = module;
    }
    
    public LibraryBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LMS_ERROR";
        this.module = "GENERAL";
    }
    
    public LibraryBusinessException(String message, String errorCode, String module, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.module = module;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getModule() {
        return module;
    }
    
    // Common business exceptions
    public static LibraryBusinessException bookNotAvailable(String bookTitle) {
        return new LibraryBusinessException(
            String.format("Book '%s' is not available for issue", bookTitle),
            "BOOK_NOT_AVAILABLE",
            "BOOK_MANAGEMENT"
        );
    }
    
    public static LibraryBusinessException userExceedsBookLimit(String username, int limit) {
        return new LibraryBusinessException(
            String.format("User '%s' has reached the maximum book limit of %d", username, limit),
            "USER_BOOK_LIMIT_EXCEEDED",
            "TRANSACTION"
        );
    }
    
    public static LibraryBusinessException bookAlreadyIssued(String bookTitle, String username) {
        return new LibraryBusinessException(
            String.format("Book '%s' is already issued to user '%s'", bookTitle, username),
            "BOOK_ALREADY_ISSUED",
            "TRANSACTION"
        );
    }
    
    public static LibraryBusinessException invalidReturnOperation(String reason) {
        return new LibraryBusinessException(
            String.format("Invalid return operation: %s", reason),
            "INVALID_RETURN",
            "TRANSACTION"
        );
    }
    
    public static LibraryBusinessException reservationNotAllowed(String reason) {
        return new LibraryBusinessException(
            String.format("Reservation not allowed: %s", reason),
            "RESERVATION_NOT_ALLOWED",
            "RESERVATION"
        );
    }
    
    public static LibraryBusinessException paymentRequired(double amount) {
        return new LibraryBusinessException(
            String.format("Payment of $%.2f is required before proceeding", amount),
            "PAYMENT_REQUIRED",
            "FINE_MANAGEMENT"
        );
    }
    
    public static LibraryBusinessException create(String message, String errorCode, String module) {
        return new LibraryBusinessException(message, errorCode, module);
    }
}