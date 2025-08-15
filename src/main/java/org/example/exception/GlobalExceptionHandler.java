package org.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(PaymentApiException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentApiException(PaymentApiException e) {
        logger.error("Payment API error: {}", e.getMessage(), e);
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of(
                    "error", e.getMessage(),
                    "statusCode", e.getStatusCode(),
                    "timestamp", System.currentTimeMillis()
                ));
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(NoHandlerFoundException e) {
        logger.error("No handler found for: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ResponseEntity.status(404)
                .body(Map.of(
                    "error", "Endpoint not found: " + e.getRequestURL(),
                    "statusCode", 404,
                    "timestamp", System.currentTimeMillis()
                ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Internal server error: " + e.getMessage(),
                    "statusCode", 500,
                    "timestamp", System.currentTimeMillis()
                ));
    }
}