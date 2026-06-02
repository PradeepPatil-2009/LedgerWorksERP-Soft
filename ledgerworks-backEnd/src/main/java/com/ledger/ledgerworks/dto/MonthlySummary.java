package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class MonthlySummary {

    private String month;
    private BigDecimal amount;

    public MonthlySummary(String month, BigDecimal amount) {
        this.month = month;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}