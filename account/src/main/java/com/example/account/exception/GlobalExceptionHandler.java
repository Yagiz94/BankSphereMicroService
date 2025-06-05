package com.example.account.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(AccountNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Account Not Found");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("timestamp", System.currentTimeMillis());
        logger.error(ex.getMessage(), "\n" + ex.getCause());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFundException(InsufficientFundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Insufficient Funds");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("timestamp", System.currentTimeMillis());
        logger.error(ex.getMessage(), "\n" + ex.getCause());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
