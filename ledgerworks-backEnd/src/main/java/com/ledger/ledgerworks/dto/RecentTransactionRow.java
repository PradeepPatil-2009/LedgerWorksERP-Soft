package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecentTransactionRow {

    private String transactionNumber;
    private String debitAccount;
    private String creditAccount;
    private BigDecimal amount;
    private LocalDate date;

    public RecentTransactionRow(String transactionNumber,
                                String debitAccount,
                                String creditAccount,
                                BigDecimal amount,
                                LocalDate date) {
        this.transactionNumber = transactionNumber;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
        this.date = date;
    }

    public String getTransactionNumber() { return transactionNumber; }
    public String getDebitAccount() { return debitAccount; }
    public String getCreditAccount() { return creditAccount; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}