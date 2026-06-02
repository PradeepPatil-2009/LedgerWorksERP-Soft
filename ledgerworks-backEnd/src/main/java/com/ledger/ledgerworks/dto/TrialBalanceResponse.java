package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.util.List;

public class TrialBalanceResponse {

    private List<TrialBalanceRow> rows;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private boolean balanced;

    public TrialBalanceResponse(
            List<TrialBalanceRow> rows,
            BigDecimal totalDebit,
            BigDecimal totalCredit,
            boolean balanced) {

        this.rows = rows;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.balanced = balanced;
    }

    public List<TrialBalanceRow> getRows() {
        return rows;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public boolean isBalanced() {
        return balanced;
    }
}