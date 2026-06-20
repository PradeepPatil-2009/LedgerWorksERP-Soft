package com.ledger.ledgerworks.entity;

import com.ledger.ledgerworks.enums.VoucherPaymentMode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt_voucher")
public class ReceiptVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String voucherNumber;

    private LocalDate date;

    // Customer from whom money is received
    private String partyName;

    private Long customerId;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private VoucherPaymentMode paymentMode = VoucherPaymentMode.CASH;

    private String reference;

    private String narration;

    private LocalDateTime createdAt;

    @PrePersist
    public void beforeSave() {

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public VoucherPaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(VoucherPaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
