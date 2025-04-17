// service/AccountService.java
package com.example.account.service;

import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountNotFoundException;
import com.example.account.model.Account;
import com.example.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // Method to retrieve account by id and userId
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    public List<AccountDto> getAllAccounts(String userName) {
        // Find the user by ID
        List<AccountDto> accounts = accountRepository.findByUserName(userName).stream().map(account -> {
            AccountDto accountDto = new AccountDto();
            accountDto.setBalance(account.getBalance());
            accountDto.setAccountType(account.getAccountType());
            return accountDto;
        }).collect(Collectors.toList());
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for the user");
        } else {
            return accounts;
        }
    }

    public Account createAccount(AccountDto accountDto, String userName) {
        if (accountDto == null) {
            throw new RuntimeException("Account attributes are required");
        }
        Account account = new Account();
        account.setUserName(userName);
        account.setBalance(accountDto.getBalance());
        account.setAccountType(accountDto.getAccountType().getValue());
        return accountRepository.save(account);
    }

    public void deleteAccount(Long accountId) {
        // Find the account associated with the user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.delete(account);
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
