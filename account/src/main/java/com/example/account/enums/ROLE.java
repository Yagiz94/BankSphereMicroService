package com.example.account.enums;

public enum ROLE {
    PERSONAL_USER(0),
    CORPORATE_USER(1),
    ADMIN(2);

    private int value;

    ROLE(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int roleValue) {
        this.value = roleValue;
    }
}
