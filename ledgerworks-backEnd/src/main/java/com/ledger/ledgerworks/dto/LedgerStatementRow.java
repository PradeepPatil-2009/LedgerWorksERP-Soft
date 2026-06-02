package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LedgerStatementRow {

    private LocalDate date;
    private String narration;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;

    public LedgerStatementRow(LocalDate date,
                              String narration,
                              BigDecimal debit,
                              BigDecimal credit,
                              BigDecimal balance) {
        this.date = date;
        this.narration = narration;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
    }

    public LocalDate getDate() { return date; }
    public String getNarration() { return narration; }
    public BigDecimal getDebit() { return debit; }
    public BigDecimal getCredit() { return credit; }
    public BigDecimal getBalance() { return balance; }
}