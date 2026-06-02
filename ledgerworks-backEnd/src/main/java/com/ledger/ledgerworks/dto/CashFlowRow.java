package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class CashFlowRow {

    private String category;
    private BigDecimal inflow;
    private BigDecimal outflow;

    public CashFlowRow(String category,
                       BigDecimal inflow,
                       BigDecimal outflow) {
        this.category = category;
        this.inflow = inflow;
        this.outflow = outflow;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getInflow() {
        return inflow;
    }

    public BigDecimal getOutflow() {
        return outflow;
    }
}