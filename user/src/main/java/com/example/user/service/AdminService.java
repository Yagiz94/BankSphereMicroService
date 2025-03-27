// service/AdminService.java
package com.example.user.service;

import com.example.user.dto.UserResponseDto;
import com.example.user.model.User;
import com.example.user.exception.UserAccountNotFoundException;
import com.example.user.exception.UserNotFoundException;
import com.example.user.exception.UserTransactionNotFoundException;
import com.example.user.repository.AccountRepository;
import com.example.user.repository.AuthRepository;
import com.example.user.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuthRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserResponseDto userResponseDto = new UserResponseDto();
                    userResponseDto.setUsername(user.getUsername());
                    userResponseDto.setEmail(user.getEmail());
                    userResponseDto.setPhone(user.getPhoneNumber());
                    userResponseDto.setRole(user.getRole());
                    return userResponseDto;
                })
                .collect(Collectors.toList());
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

    public void deleteAccount(Long accountId) {
        // Implement the logic to delete an account
        if (accountRepository.existsById(accountId)) {
            accountRepository.deleteById(accountId);
        } else {
            throw new UserAccountNotFoundException("Account not found, delete is not successful");
        }
    }

    public void deleteTransaction(Long transactionId) {
        // Implement the logic to delete a transaction
        if (transactionRepository.existsById(transactionId)) {
            transactionRepository.deleteById(transactionId);
        } else {
            throw new UserTransactionNotFoundException("Transaction not found, delete is not successful");
        }
    }
}
