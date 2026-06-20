package com.ledger.ledgerworks.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DocumentType {

    INVOICE,
    PURCHASE,
    DELIVERY_CHALLAN,
    RECEIPT,
    PAYMENT,
    JOURNAL,
    CREDIT_NOTE,
    DEBIT_NOTE,
    CONTRA;

    @JsonCreator
    public static DocumentType fromString(String value) {

        if (value == null) {
            return null;
        }

        return DocumentType.valueOf(value.trim().toUpperCase());
    }
}
