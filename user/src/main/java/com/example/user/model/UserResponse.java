package com.example.user.model;

public class UserResponse {
    private String token;
    private String message;

    // Error Case
    public UserResponse(String message) {
        this.message = message;
    }

    // Tokenization Case
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
    // Getters and Setters (or use @Data from Lombok)
}