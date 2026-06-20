package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InvoicePaymentRequest {

    @NotBlank
    private String invoiceNumber;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String paymentMode;

    private String reference;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(
            String invoiceNumber
    ) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(
            String paymentMode
    ) {
        this.paymentMode = paymentMode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(
            String reference
    ) {
        this.reference = reference;
    }
}