package com.ledger.ledgerworks.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ledger.ledgerworks.enums.TransactionType;

import jakarta.persistence.*;

@Entity
@Table(name = "ledger_transaction")
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Transaction Number
    @Column(unique = true, nullable = false)
    private String transactionNumber;

    // 🔹 Debit Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_account_id", nullable = false)
    private LedgerAccount debitAccount;

    // 🔹 Credit Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_account_id", nullable = false)
    private LedgerAccount creditAccount;

    // 🔹 Amount
    @Column(nullable = false)
    private BigDecimal amount;

    // 🔹 Description
    private String description;

    // 🔹 Narration
    private String narration;

    // 🔹 Transaction Date
    @Column(nullable = false)
    private LocalDate transactionDate;

    // 🔹 Transaction Type
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType = TransactionType.INVOICE;

    // 🔹 Reversal Transaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_of_id")
    private LedgerTransaction reversalOf;

    private boolean reversed = false;

    // 🔹 Audit Fields
    private LocalDateTime createdAt;

    private String createdBy;

    // 🔹 Invoice Link
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    // =====================================================
    // AUTO GENERATION BEFORE INSERT
    // =====================================================

    @PrePersist
    public void beforeSave() {

        if (this.transactionNumber == null) {
            this.transactionNumber = "TXN-" + System.currentTimeMillis();
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.transactionDate == null) {
            this.transactionDate = LocalDate.now();
        }
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
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

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LedgerTransaction getReversalOf() {
        return reversalOf;
    }

    public void setReversalOf(LedgerTransaction reversalOf) {
        this.reversalOf = reversalOf;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}