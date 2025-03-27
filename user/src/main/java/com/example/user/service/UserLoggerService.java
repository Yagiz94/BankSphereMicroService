package com.example.user.service;

import com.example.bankSphere.entity.UserLogger;
import com.example.bankSphere.repository.UserLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoggerService {
    private final UserLoggerRepository userLoggerRepository;

    @Autowired
    public UserLoggerService(UserLoggerRepository userLoggerRepository) {
        this.userLoggerRepository = userLoggerRepository;
    }

    public UserLogger saveUserLogger(UserLogger userLogger) {
        return userLoggerRepository.save(userLogger);
    }
}