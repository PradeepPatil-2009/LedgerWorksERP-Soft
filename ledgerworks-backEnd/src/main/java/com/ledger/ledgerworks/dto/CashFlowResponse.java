package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.util.List;

public class CashFlowResponse {

    private List<CashFlowRow> rows;
    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
    private BigDecimal netCashFlow;

    public CashFlowResponse(List<CashFlowRow> rows,
                            BigDecimal totalInflow,
                            BigDecimal totalOutflow,
                            BigDecimal netCashFlow) {
        this.rows = rows;
        this.totalInflow = totalInflow;
        this.totalOutflow = totalOutflow;
        this.netCashFlow = netCashFlow;
    }

    public List<CashFlowRow> getRows() {
        return rows;
    }

    public BigDecimal getTotalInflow() {
        return totalInflow;
    }

    public BigDecimal getTotalOutflow() {
        return totalOutflow;
    }

    public BigDecimal getNetCashFlow() {
        return netCashFlow;
    }
}