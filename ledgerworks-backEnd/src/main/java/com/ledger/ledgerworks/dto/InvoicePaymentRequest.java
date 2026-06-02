package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class InvoicePaymentRequest {

    private String invoiceNumber;

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