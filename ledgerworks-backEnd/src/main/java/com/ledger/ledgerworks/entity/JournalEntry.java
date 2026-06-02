package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate entryDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "debit_account_id")
    private LedgerAccount debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id")
    private LedgerAccount creditAccount;

    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LedgerAccount getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(LedgerAccount debitAccount) {
        this.debitAccount = debitAccount;
    }

    public LedgerAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(LedgerAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}