package com.example.user.service;

import com.example.user.exception.UserNotFoundException;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUserByUserName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        } else {
            return user;
        }
    }

    // Additional administrative functions (user management, logs, etc.)
    public void deleteUser(Long userId) {
        // Implement the logic to delete a user
        if (userRepository.existsById(String.valueOf(userId))) {
            userRepository.deleteById(String.valueOf(userId));
        } else {
            throw new UserNotFoundException("User not found, delete is not successful");
        }
    }
}
