// exception/GlobalExceptionHandler.java
package com.example.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InsufficientFundException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFundException(InsufficientFundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Insufficient Funds");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
