package com.example.transaction.enums;

public enum KYC_STATUS {
    PENDING(0),
    VERIFIED(1),
    REJECTED(2);

    private int value;

    KYC_STATUS(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int setValue(int value) {
        return this.value = value;
    }
}