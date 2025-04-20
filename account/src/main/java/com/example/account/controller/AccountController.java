package com.example.account.controller;

import com.example.account.dto.AccountDto;
import com.example.account.model.Account;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccounts(@RequestHeader(value = "userName") String userName) {
        List<AccountDto> accounts = accountService.getAllAccounts(userName);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/validate/{accountId}")
    public ResponseEntity<?> validateAccount(@RequestHeader(value = "userName") String userName, @PathVariable Long accountId) {
        boolean response = accountService.validateAccount(userName, accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestHeader(value = "userName") String userName, @RequestBody AccountDto account) {
        Account newAccount = accountService.createAccount(account, userName);
        return ResponseEntity.ok("Account created successfully. " + newAccount);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getAccountBalance(@RequestHeader("userName") String userName, @PathVariable Long accountId) {
        BigDecimal currentBalance = accountService.getAccountBalance(userName, accountId);
        return ResponseEntity.ok(currentBalance);
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteAccount(@RequestHeader(value = "userName") String userName, @PathVariable Long accountId) {
        accountService.deleteAccount(accountId, userName);
        return ResponseEntity.ok("Account deleted successfully");
    }
}