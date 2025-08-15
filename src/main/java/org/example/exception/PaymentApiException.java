package org.example.exception;

public class PaymentApiException extends RuntimeException {
    private final int statusCode;
    
    public PaymentApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public PaymentApiException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}