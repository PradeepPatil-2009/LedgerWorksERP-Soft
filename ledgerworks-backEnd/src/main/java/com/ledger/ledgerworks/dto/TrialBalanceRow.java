package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class TrialBalanceRow {

    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;

    public TrialBalanceRow(String accountName, BigDecimal debit2, BigDecimal credit2) {
        this.accountName = accountName;
        this.debit = debit2;
        this.credit = credit2;
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }
}
