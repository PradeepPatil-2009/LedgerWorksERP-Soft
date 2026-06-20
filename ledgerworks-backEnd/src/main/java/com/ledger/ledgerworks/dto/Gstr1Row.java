package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One outward-supply row for a GSTR-1 export, built from an invoice.
 */
public class Gstr1Row {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerName;
    private String customerGstin;
    private String placeOfSupply;
    private BigDecimal taxableValue;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal total;
    private BigDecimal rate;

    public Gstr1Row() {
    }

    public Gstr1Row(
            String invoiceNumber,
            LocalDate invoiceDate,
            String customerName,
            String customerGstin,
            String placeOfSupply,
            BigDecimal taxableValue,
            BigDecimal cgst,
            BigDecimal sgst,
            BigDecimal igst,
            BigDecimal total,
            BigDecimal rate
    ) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.customerGstin = customerGstin;
        this.placeOfSupply = placeOfSupply;
        this.taxableValue = taxableValue;
        this.cgst = cgst;
        this.sgst = sgst;
        this.igst = igst;
        this.total = total;
        this.rate = rate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerGstin() {
        return customerGstin;
    }

    public void setCustomerGstin(String customerGstin) {
        this.customerGstin = customerGstin;
    }

    public String getPlaceOfSupply() {
        return placeOfSupply;
    }

    public void setPlaceOfSupply(String placeOfSupply) {
        this.placeOfSupply = placeOfSupply;
    }

    public BigDecimal getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(BigDecimal taxableValue) {
        this.taxableValue = taxableValue;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public void setCgst(BigDecimal cgst) {
        this.cgst = cgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public void setSgst(BigDecimal sgst) {
        this.sgst = sgst;
    }

    public BigDecimal getIgst() {
        return igst;
    }

    public void setIgst(BigDecimal igst) {
        this.igst = igst;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
