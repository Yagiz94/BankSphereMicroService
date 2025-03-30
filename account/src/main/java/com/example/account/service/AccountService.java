// service/AccountService.java
package com.example.account.service;

import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountNotFoundException;
import com.example.account.model.Account;
import com.example.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}") // Example: "http://localhost:8081"
    private String userServiceUrl;

    // Method to retrieve account by id and userId
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    public List<AccountDto> getAllAccounts(Long userId) {
        // Find the user by ID
        List<AccountDto> accounts = accountRepository.findByUserId(userId).stream().map(account -> {
            AccountDto accountDto = new AccountDto();
            accountDto.setAccountId(account.getId());
            accountDto.setUserName(account.getUserName());
            accountDto.setBalance(account.getBalance());
            accountDto.setAccountType(account.getAccountType());
            return accountDto;
        }).collect(Collectors.toList());
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for user id: " + userId);
        } else {
            return accounts;
        }
    }

    public Account createAccount(AccountDto accountDto) {
        if (accountDto == null || accountDto.getUserName() == null) {
            throw new RuntimeException("User name is required");
        }
        if (!isUserValid(accountDto.getUserName())) {
            throw new RuntimeException("User is invalid");
        } else {
            Account account = new Account();
            account.setUserName(accountDto.getUserName());
            account.setBalance(accountDto.getBalance());
            account.setAccountType(accountDto.getAccountType().getValue());
            return accountRepository.save(account);
        }
    }

    public void deleteAccount(Long accountId) {
        // Find the account associated with the user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.delete(account);
    }

    public boolean isUserValid(String userName) {
        try {
            String url = userServiceUrl + "/api/user/validate/" + userName;
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody()); // Ensure proper Boolean handling
        } catch (Exception e) {
            return false; // If request fails, assume user is invalid
        }
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount, String requestingUserName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        // Validate ownership: Ensure the account belongs to the user making the request.
        if (!account.getUserName().equals(requestingUserName)) {
            throw new RuntimeException("Unauthorized: This account does not belong to the user");
        }

        // Add the deposit amount to the current balance.
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    // Similarly, for withdrawal:
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount, String requestingUserName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate ownership.
        if (!account.getUserName().equals(requestingUserName)) {
            throw new RuntimeException("Unauthorized: This account does not belong to the user");
        }

        // Check if sufficient balance exists.
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Deduct the amount.
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }
}
