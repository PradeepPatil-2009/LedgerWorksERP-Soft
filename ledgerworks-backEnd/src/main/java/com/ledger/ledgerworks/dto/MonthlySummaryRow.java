package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class MonthlySummaryRow {

    private String month;
    private BigDecimal income;
    private BigDecimal expense;

    public MonthlySummaryRow(String month,
                             BigDecimal income,
                             BigDecimal expense) {
        this.month = month;
        this.income = income;
        this.expense = expense;
    }

    public String getMonth() { return month; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
}