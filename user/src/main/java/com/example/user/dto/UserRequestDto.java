package com.example.user.dto;

public class UserRequestDto {
    private String username;
    private String email;
    private int role;
    private String phone;
    private String password;

    public UserRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public int getRole() {
        return role;
    }
}
