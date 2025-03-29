package com.example.user.controller;

import com.example.user.dto.AdminDto;
import com.example.user.dto.UserRequestDto;
import com.example.user.exception.UserAlreadyExistsException;
import com.example.user.exception.UserFieldsMissingException;
import com.example.user.exception.UserLoginCredentialsInvalidException;
import com.example.user.exception.UserNotFoundException;
import com.example.user.model.User;
import com.example.user.model.UserLogger;
import com.example.user.model.UserResponse;
import com.example.user.service.AuthService;
import com.example.user.service.JwtRedisService;
import com.example.user.service.UserLoggerService;
import com.example.user.service.UserService;
import com.mongodb.MongoSocketException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService userAuthService;

    @Autowired
    private UserLoggerService userLoggerService;

    @Autowired
    private JwtRedisService jwtRedisService; // Inject the service

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDto userDto) {

        // check if the request body is missing
        if (userDto == null) {
            return ResponseEntity.badRequest().body("Missing request body!");
        }

        // check if the request body contains missing fields
        String missingFields = getMissingFields(userDto);
        if (!missingFields.isEmpty()) {
            throw new UserFieldsMissingException("Missing fields: " + missingFields);
        }

        // register a new user with a new account and log the registry
        User user;
        try {
            user = userAuthService.registerUser(userDto);
        } catch (RuntimeException e) {
            throw new UserAlreadyExistsException("User already exists!");
        }
        try {
            userLoggerService.saveUserLogger(
                    new UserLogger(user.getUsername(), user.getEmail(), Instant.now().toString(), "New User"));
        } catch (MongoSocketException e) {
            System.out.println("Something went wrong with logging but user registration is successful");
        }

        // Generate a token
        String jwtToken = generateToken(user.getUsername());

        // Convert the token to a secret key
        String secretKeyBase64 = Base64.getEncoder().encodeToString(jwtToken.getBytes());

        // Save the secret key in Redis
        jwtRedisService.saveSecretKey(user.getUsername(), secretKeyBase64);

        return ResponseEntity.ok("{\n" +
                "\t\"message\": \"User registered successfully!\"" + "\n" +
                "\t\"token\": \"" + jwtToken + "\"\n}");
    }

    // check missing JSON attributes (fields)
    private String getMissingFields(UserRequestDto userDto) {
        List<String> missingFields = new ArrayList<>();
        if (userDto.getUsername() == null) missingFields.add("username");
        if (userDto.getPassword() == null) missingFields.add("password");
        if (userDto.getPhone() == null) missingFields.add("phone number");
        return String.join(", ", missingFields);
    }

    // Login endpoint would typically validate credentials and return a JWT token.
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequestDto userDto) {

        //Validate the login credentials
        User user = userAuthService.retrieveUserByName(userDto.getUsername());

        //Check whether the user exists
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        // Validate the password
        else if (!user.getPassword().equals(userDto.getPassword())) {
            throw new UserLoginCredentialsInvalidException("Invalid login credentials!");
        }
        // If everything is valid
        return ResponseEntity.ok(new UserResponse("Login successful!"));
    }

    private String generateToken(String username) {
        // Generate or retrieve the secret key
        String secretKeyBase64 = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        // Decode Base64 secret key
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
        Key secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        // Create JWT token using claims and signing with the secure key
        String token = Jwts.builder()
                .setSubject(username)  // Use 'sub' for username
                .claim("username", username)  // Explicitly add 'username' to claims
                .setIssuedAt(new Date())  // Correct way to set 'iat'
                .signWith(secretKey)  // Sign with user-specific secret key
                .compact();

        // Debug prints
        System.out.println("Generated token: " + token);
        System.out.println("Secret key length: " + secretKeyBytes.length);
        System.out.println("Secret key (Base64): " + secretKeyBase64);

        return token;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/validate/{id}")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id) != null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("senderID") Long senderId) {
        User user = userService.getUser(senderId);
        if (user.getRole().getValue() != 2) {
            return ResponseEntity.badRequest().body("You are not authorized to delete a user");
        } else {
            return ResponseEntity.ok(userService.getAllUsers());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody AdminDto adminDto) {
        User user = userService.getUser(adminDto.getRequestUserID());
        if (user.getRole().getValue() != 2) {
            return ResponseEntity.badRequest().body("You are not authorized to delete a user");
        } else {
            userService.deleteUser(adminDto.getTargetUserID());
            // remove user token from Redis
            jwtRedisService.removeSecretKey(adminDto.getTargetUserID().toString());
            System.out.println("User deleted successfully.");
            return ResponseEntity.ok("User deleted successfully");
        }
    }
}
