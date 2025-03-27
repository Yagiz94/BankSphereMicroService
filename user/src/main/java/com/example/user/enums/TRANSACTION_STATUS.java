package com.example.user.enums;

public enum TRANSACTION_STATUS {
    FAILED(-1),
    PENDING(0),
    SUCCESS(1);

    private int value;

    TRANSACTION_STATUS(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int setValue(int value) {
        return this.value = value;
    }

}
