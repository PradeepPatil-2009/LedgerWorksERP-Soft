package com.ledger.ledgerworks.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccountType {

    ASSET,
    LIABILITY,
    CAPITAL,
    INCOME,
    EXPENSE;

    @JsonCreator
    public static AccountType fromString(String value) {

        if (value == null) {
            return null;
        }

        return AccountType.valueOf(value.trim().toUpperCase());
    }
}