package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GeneralLedgerRow {

    private LocalDate date;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;

    public GeneralLedgerRow(LocalDate date, String description,
                            BigDecimal debit, BigDecimal credit,
                            BigDecimal balance) {
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}