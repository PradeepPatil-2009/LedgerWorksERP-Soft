package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class ProfitLossResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfit;

    public ProfitLossResponse(BigDecimal totalIncome,
                              BigDecimal totalExpense,
                              BigDecimal netProfit) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netProfit = netProfit;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }
}