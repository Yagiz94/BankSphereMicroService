package com.example.user.exception;

public class UserFieldsMissingException extends RuntimeException {
    public UserFieldsMissingException(String message) {
        super(message);
    }
}
