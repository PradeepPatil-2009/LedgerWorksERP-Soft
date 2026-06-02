package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class GstSummaryDto {

    private BigDecimal taxable;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal grandTotal;

    public GstSummaryDto(
            BigDecimal taxable,
            BigDecimal cgst,
            BigDecimal sgst,
            BigDecimal igst,
            BigDecimal grandTotal
    ) {
        this.taxable = taxable;
        this.cgst = cgst;
        this.sgst = sgst;
        this.igst = igst;
        this.grandTotal = grandTotal;
    }

    public BigDecimal getTaxable() {
        return taxable;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public BigDecimal getIgst() {
        return igst;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }
}