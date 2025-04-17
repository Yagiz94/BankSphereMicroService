package com.example.account.dto;

import com.example.account.enums.ACCOUNT_TYPE;

import java.math.BigDecimal;

public class AccountDto {

    private BigDecimal balance;
    private ACCOUNT_TYPE accountType;

    // Default constructor for JPA
    public AccountDto() {
    }

    // Getters and Setters
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    public void setAccountType(ACCOUNT_TYPE accountType) {
        this.accountType = accountType;
    }
}
