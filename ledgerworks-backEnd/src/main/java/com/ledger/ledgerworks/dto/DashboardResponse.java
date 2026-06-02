package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DashboardResponse {

    private BigDecimal totalSales;
    private BigDecimal totalOutstanding;
    private BigDecimal overdueAmount;
    private int overdueCount;

    public DashboardResponse(BigDecimal totalSales,
                             BigDecimal totalOutstanding,
                             BigDecimal overdueAmount,
                             int overdueCount) {

        this.totalSales = scale(totalSales);
        this.totalOutstanding = scale(totalOutstanding);
        this.overdueAmount = scale(overdueAmount);
        this.overdueCount = overdueCount;
    }

    private BigDecimal scale(BigDecimal val) {
        return val != null ? val.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getTotalSales() { return totalSales; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public BigDecimal getOverdueAmount() { return overdueAmount; }
    public int getOverdueCount() { return overdueCount; }
}