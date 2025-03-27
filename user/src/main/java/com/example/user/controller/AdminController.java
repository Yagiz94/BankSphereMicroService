// controller/AdminController.java
package com.example.user.controller;

import com.example.user.dto.UserResponseDto;
import com.example.user.model.Account;
import com.example.user.service.AccountService;
import com.example.user.service.AdminService;
import com.example.user.service.JwtRedisService;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    JwtRedisService jwtRedisService;

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        // remove user token from Redis
        jwtRedisService.removeSecretKey(id.toString());
        System.out.println("User deleted successfully.");
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/{userId}/account")
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
            e.printStackTrace(); // Optional: print the stack trace for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        System.out.println("Account deleted");
        adminService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        System.out.println("Transaction deleted.");
        adminService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
