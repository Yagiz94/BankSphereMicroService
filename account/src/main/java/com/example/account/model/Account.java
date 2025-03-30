package com.example.account.model;

import com.example.account.enums.ACCOUNT_TYPE;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @Column(name = "userName", nullable = false)
    private String userName;
    @Enumerated(EnumType.STRING)
    @Column(name = "accountType", nullable = false)
    private ACCOUNT_TYPE accountType;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    // Default constructor for JPA
    public Account() {
    }

    // Getters and Setters
    public Long getId() {
        return accountId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    // Method to set accountType based on integer value
    public void setAccountType(int accountType) {
        this.accountType = ACCOUNT_TYPE.values()[accountType];  // Set accountType based on enum index
    }

    @Override
    public String toString() {
        return "\"Account\":{" +
                "\n\"accountType\": \"" + accountType + "\", \n\"balance\": " + balance + ", " + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(accountId);
    }

}
