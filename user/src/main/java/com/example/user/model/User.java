package com.example.user.model;

import com.example.user.enums.KYC_STATUS;
import com.example.user.enums.ROLE;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNumber", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "role", nullable = false)
    private ROLE role;

    // KYC status: PENDING, VERIFIED, or REJECTED
    @Column(name = "kycStatus", nullable = false)
    private KYC_STATUS kycStatus;

    public User() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(int roleValue) {
        this.role = ROLE.values()[roleValue];
    }

    public void setKycStatus(int kycStatus) {
        this.kycStatus = KYC_STATUS.values()[kycStatus];
    }

    public KYC_STATUS getKycStatus() {
        return kycStatus;
    }

    @Override
    public String toString() {
        return "\"User\":" + "{" +
                "\n\"username\":" + username +
                ", \n\"email\":" + email +
                ", \n\"phoneNumber\":" + phoneNumber +
                ", \n\"role\":" + role +
                ", \n\"kycStatus\":" + kycStatus +
                '}';
    }

}
