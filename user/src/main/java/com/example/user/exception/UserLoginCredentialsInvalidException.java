package com.example.user.exception;

public class UserLoginCredentialsInvalidException extends RuntimeException{
    public UserLoginCredentialsInvalidException(String message) {
        super(message);
    }
}
