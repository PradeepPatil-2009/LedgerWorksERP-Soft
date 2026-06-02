package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceOutstandingRow {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerName;
    private BigDecimal grandTotal;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String status;

    public InvoiceOutstandingRow(String invoiceNumber,
                                  LocalDate invoiceDate,
                                  String customerName,
                                  BigDecimal grandTotal,
                                  BigDecimal paidAmount,
                                  BigDecimal balanceAmount,
                                  String status) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.grandTotal = grandTotal;
        this.paidAmount = paidAmount;
        this.balanceAmount = balanceAmount;
        this.status = status;
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getCustomerName() { return customerName; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public String getStatus() { return status; }
}