// controller/AccountController.java
package com.example.account.controller;

import com.example.account.dto.AccountDto;
import com.example.account.model.Account;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccounts(@RequestHeader(value = "userName") String userName) {
        List<AccountDto> accounts = accountService.getAllAccounts(userName);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestHeader(value = "userName") String userName, @RequestBody AccountDto account) {
        Account newAccount = accountService.createAccount(account, userName);
        return ResponseEntity.ok("Account created successfully. " + newAccount);
    }

//    @PutMapping("/update/{accountId}")
//    public ResponseEntity<?> updateAccount(@RequestHeader(value = "userName") String userName, @PathVariable Long accountId, @RequestBody AccountDto account) {
//        Account updatedAccount = accountService.updateAccount(userName, accountId, account);
//        return ResponseEntity.ok("Account updated successfully. " + updatedAccount);
//    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteAccount(@RequestHeader(value = "userName") String userName, @PathVariable Long accountId) {
        accountService.deleteAccount(accountId, userName);
        return ResponseEntity.ok("Account deleted successfully");
    }
}