package com.ledger.ledgerworks.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VoucherPaymentMode {

    CASH,
    BANK,
    UPI;

    @JsonCreator
    public static VoucherPaymentMode fromString(String value) {

        if (value == null) {
            return null;
        }

        return VoucherPaymentMode.valueOf(value.trim().toUpperCase());
    }
}
