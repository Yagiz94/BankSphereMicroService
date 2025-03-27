package com.example.user.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordEncoder {
    public String encode(String password) {
        return password;
    }
}
