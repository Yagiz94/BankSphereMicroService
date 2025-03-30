package com.example.account.dto;

import com.example.account.enums.ACCOUNT_TYPE;

import java.math.BigDecimal;

public class AccountDto {

    private Long accountId;
    private String userName;
    private BigDecimal balance;
    private ACCOUNT_TYPE accountType;

    // Default constructor for JPA
    public AccountDto() {
    }

    // Getters and Setters

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
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

    public void setAccountType(ACCOUNT_TYPE accountType) {
        this.accountType = accountType;
    }
}
