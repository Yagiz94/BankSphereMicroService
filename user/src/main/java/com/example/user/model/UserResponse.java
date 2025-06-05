package com.example.user.model;

public class UserResponse {
    private String token;
    private String message;

    public UserResponse(String message) {
        this.message = message;
    }

    public UserResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
