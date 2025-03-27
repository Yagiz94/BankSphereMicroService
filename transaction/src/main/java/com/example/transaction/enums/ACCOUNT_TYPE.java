package com.example.transaction.enums;

public enum ACCOUNT_TYPE {
    DemandDepositAccount(0),
    TimeDepositAccount(1),
    InvestmentAccount(2),
    GoldAccount(3),
    ForeignCurrencyAccount(4),
    SavingsAccount(5),
    CreditAccount(6),
    PrivatePensionAccount(7);

    private int value;

    ACCOUNT_TYPE(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int setValue(int value) {
        return this.value = value;
    }
}
