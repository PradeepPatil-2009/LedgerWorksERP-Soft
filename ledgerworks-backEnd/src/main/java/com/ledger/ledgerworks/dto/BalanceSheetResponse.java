package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class BalanceSheetResponse {

    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal netProfit;
    private boolean balanced;

    public BalanceSheetResponse(BigDecimal totalAssets,
                                BigDecimal totalLiabilities,
                                BigDecimal netProfit,
                                boolean balanced) {
        this.totalAssets = totalAssets;
        this.totalLiabilities = totalLiabilities;
        this.netProfit = netProfit;
        this.balanced = balanced;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public boolean isBalanced() {
        return balanced;
    }
}