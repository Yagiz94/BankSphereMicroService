package com.example.user.dto;

import com.example.user.enums.ROLE;

public class UserResponseDto {
    private String username;
    private String email;
    private ROLE role;
    private String phone;

    public UserResponseDto() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public ROLE getRole() {
        return role;
    }

}
