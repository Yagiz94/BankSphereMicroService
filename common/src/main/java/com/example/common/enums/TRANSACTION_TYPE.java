package com.example.common.enums;

public enum TRANSACTION_TYPE {

    Withdraw(0),
    Deposit(1);

    private int value;

    TRANSACTION_TYPE(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int setValue(int value) {
        return this.value = value;
    }

}





