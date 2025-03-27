// service/AccountService.java
package com.example.account.service;

import com.example.account.dto.TransactionDto;
import com.example.account.model.Account;
import com.example.account.model.Transaction;
import com.example.account.model.User;
import com.example.account.exception.UserAccountNotFoundException;
import com.example.account.exception.UserNotFoundException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    // Method to retrieve account by id and userId
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account getAccountByUserId(Long userId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User id not found"));

        // Find the account associated with the user
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found for the given user"));
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Long userId, Long accountId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User id not found"));

        // Find the account associated with the user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Check if the account belongs to the user
        if (account.getUser().getId().equals(userId)) {
            accountRepository.delete(account);
        } else {
            throw new RuntimeException("Account deletion failed.");
        }
    }

    public List<TransactionDto> getAllTransactions(Long userId, Long accountId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Find the account associated with the user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));

        // Check if the account belongs to the user
        if (account.getUser().getId().equals(userId)) {
            return transactionService.getAllTransactions(account.getId()).stream()
                    .map(transaction -> {
                        TransactionDto transactionDto = new TransactionDto();
                        transactionDto.setType(transaction.getType().getValue());
                        transactionDto.setTimestamp(transaction.getTimestamp());
                        transactionDto.setAmount(transaction.getAmount());
                        transactionDto.setStatus(transaction.getStatus());
                        return transactionDto;
                    })
                    .collect(Collectors.toList());
        } else {
            throw new UserAccountNotFoundException("Account not found for the user.");
        }
    }

    public Account retrieveAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found."));
    }

    public void withdraw(Transaction transaction) {
        transactionService.withdraw(transaction);
    }

    public void deposit(Transaction transaction) {
        transactionService.deposit(transaction);
    }
}
