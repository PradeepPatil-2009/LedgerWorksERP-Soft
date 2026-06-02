package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class AgingReportRow {

    private String invoiceNumber;
    private String customerName;
    private BigDecimal balanceAmount;
    private long daysOutstanding;

    public AgingReportRow(String invoiceNumber,
                          String customerName,
                          BigDecimal balanceAmount,
                          long daysOutstanding) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.balanceAmount = balanceAmount;
        this.daysOutstanding = daysOutstanding;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public long getDaysOutstanding() {
        return daysOutstanding;
    }
}