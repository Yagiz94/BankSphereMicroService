package com.example.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TRANSACTION_TYPE {
    DEPOSIT, WITHDRAWAL;

    @JsonCreator
    public static TRANSACTION_TYPE from(String value) {
        return TRANSACTION_TYPE.valueOf(value.toUpperCase());
    }

    // Optional: serialize using name()
    @JsonValue
    public String toValue() {
        return this.name();
    }
}





