package com.example.account.service;

import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountNotFoundException;
import com.example.account.exception.InsufficientFundException;
import com.example.account.model.Account;
import com.example.account.repository.AccountRepository;
import com.example.common.enums.TRANSACTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    private static final Logger logger = LogManager.getLogger(AccountService.class);


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

    public boolean validateAccount(String userName, Long accountId) {
        boolean response = false;
        // Find the account associated with the user
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        // Check if the account belongs to the user
        response = account != null && account.getUserName().equals(userName);
        return response;
    }

    public Account createAccount(AccountDto accountDto, String userName) {
        if (accountDto == null) {
            throw new AccountNotFoundException("Account attributes are required");
        }
        Account account = new Account();
        account.setUserName(userName);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountDto.getAccountType().getValue());
        return accountRepository.save(account);
    }

    public void updateAccount(Long accountId, BigDecimal amount, TRANSACTION_TYPE transactionType, String userName) {
        // Retrieve the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        // Check if the account belongs to the user
        if (!(account.getUserName().equals(userName))) {
            throw new AccountNotFoundException("Unauthorized. Account not found");
        }
        if (transactionType.equals(TRANSACTION_TYPE.WITHDRAWAL) && account.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundException("Insufficient funds");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        logger.info("Account update successfully");
    }

    public BigDecimal getAccountBalance(String userName, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        // Check if the account belongs to the user
        if (!(account.getUserName().equals(userName))) {
            throw new AccountNotFoundException("Unauthorized. Account not found");
        }
        return account.getBalance();
    }

    public void deleteAccount(Long accountId, String userName) {
        // Find the account associated with the user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        if (!(account.getUserName().equals(userName))) {
            throw new AccountNotFoundException("Unauthorized. Account not found");
        }
        accountRepository.delete(account);
    }
}
