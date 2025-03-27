package com.example.user.enums;

public enum TRANSACTION_TYPE {

    Transfer(0),
    BillPayment(1),
    Deposit(2),
    Withdrawal(3),
    Purchase(4),
    Refund(5),
    Fee(6),
    Interest(7),
    Other(8);

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





