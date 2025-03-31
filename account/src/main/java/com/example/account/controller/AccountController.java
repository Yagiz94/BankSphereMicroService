// controller/AccountController.java
package com.example.account.controller;

import com.example.account.dto.AccountDto;
import com.example.account.model.Account;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    private String userServiceUrl;

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<?> getAllAccounts(@PathVariable Long userId) {
        List<AccountDto> accounts = accountService.getAllAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountDto account) {
        Account newAccount = accountService.createAccount(account);
        return ResponseEntity.ok("Account created successfully. " + newAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok("Account deleted successfully");
    }


    //TODO requestUserName is not validated in the method
    // to be validated using spring security Authentication object
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount, @PathVariable String requestUserName) {
        // Validate and retrieve account objects for processing transaction
        Account account = accountService.getAccountById(accountId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity<>("Amount must be greater than zero.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Perform the deposit
            accountService.deposit(accountId, amount, requestUserName);

            // Return the updated account
            return ResponseEntity.ok("Deposit operation is successful");
        } catch (Exception e) {
            // Log the exception (use a logger for production)
            return new ResponseEntity<>("Error processing the deposit.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount, @PathVariable String requestUserName) {
        // Validate and retrieve account objects for processing transaction
        Account account = accountService.getAccountById(accountId);

        // Check if the user & account exist and account belongs to the user
        if (account.getUserName().equals(requestUserName)) {
            // Simple validation checks for the withdrawal request
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be greater than zero.", HttpStatus.BAD_REQUEST);
            }

            // Ensure the account has enough balance for the withdrawal
            if (account.getBalance().compareTo(amount) < 0) {
                return new ResponseEntity<>("Insufficient funds.", HttpStatus.BAD_REQUEST);
            }

            try {
                // Process the withdrawal by calling the service method
                accountService.withdraw(accountId, amount, requestUserName);

                // Return the updated account information after withdrawal
                return ResponseEntity.ok("Withdraw operation is successful");
            } catch (Exception e) {
                // Log the exception (use a logger for production)
                return new ResponseEntity<>("Error processing the withdrawal.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Transaction operation failed", HttpStatus.NOT_FOUND);
        }
    }
}