package com.example.user.controller;

import com.example.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    // TODO
    @PostMapping("/{userId}/accounts")
    public ResponseEntity<String> createAccount(@PathVariable Long userId, @RequestBody Account account) {
        try {
            // Ensure the user ID is set in the AccountDto
            account.setUserById(userId, userService.getUserRepository());

            // Create the account (this will save the account in the database)
            Account createdAccount = accountService.createAccount(account);

            // Return the created account with the initial deposit
            return ResponseEntity.ok("A new " + createdAccount.getAccountType() + " account has been created successfully.");

        } catch (Exception e) {
            // Log the error and return a generic error response without a message
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ResponseEntity<?> returns List<AccountDto> if the list is not empty, otherwise it returns "list is empty" message
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable Long userId) {
        User user = userService.retrieveUser(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }

        try {
            // Retrieve all accounts associated with the user
            List<AccountDto> accounts = userService.getUserAccounts(userId);
            if (accounts.isEmpty()) {
                return ResponseEntity.ok(Map.of("Message", "No accounts found for the user."));
            }
            return ResponseEntity.ok(accounts);
        } catch (RuntimeException e) {
            // If user is not found, return 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId, @PathVariable Long accountId) {
        try {
            // Delete the account with the specified ID
            accountService.deleteAccount(userId, accountId);
            return ResponseEntity.ok("Account has been deleted successfully.");
        } catch (Exception e) {
            // Log the error and return a generic error response without a message
            return ResponseEntity.internalServerError().body(Map.of("Error", "An error occurred while deleting the account."));
        }
    }
}
