package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

/**
 * Aggregated outward-supply totals for a GSTR-3B summary over a period.
 */
public class Gstr3bSummary {

    private BigDecimal totalTaxableValue = BigDecimal.ZERO;
    private BigDecimal totalCgst = BigDecimal.ZERO;
    private BigDecimal totalSgst = BigDecimal.ZERO;
    private BigDecimal totalIgst = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private long invoiceCount = 0;

    public Gstr3bSummary() {
    }

    public Gstr3bSummary(
            BigDecimal totalTaxableValue,
            BigDecimal totalCgst,
            BigDecimal totalSgst,
            BigDecimal totalIgst,
            BigDecimal totalTax,
            long invoiceCount
    ) {
        this.totalTaxableValue = totalTaxableValue;
        this.totalCgst = totalCgst;
        this.totalSgst = totalSgst;
        this.totalIgst = totalIgst;
        this.totalTax = totalTax;
        this.invoiceCount = invoiceCount;
    }

    public BigDecimal getTotalTaxableValue() {
        return totalTaxableValue;
    }

    public void setTotalTaxableValue(BigDecimal totalTaxableValue) {
        this.totalTaxableValue = totalTaxableValue;
    }

    public BigDecimal getTotalCgst() {
        return totalCgst;
    }

    public void setTotalCgst(BigDecimal totalCgst) {
        this.totalCgst = totalCgst;
    }

    public BigDecimal getTotalSgst() {
        return totalSgst;
    }

    public void setTotalSgst(BigDecimal totalSgst) {
        this.totalSgst = totalSgst;
    }

    public BigDecimal getTotalIgst() {
        return totalIgst;
    }

    public void setTotalIgst(BigDecimal totalIgst) {
        this.totalIgst = totalIgst;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public long getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(long invoiceCount) {
        this.invoiceCount = invoiceCount;
    }
}
