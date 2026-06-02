package com.ledger.ledgerworks.dto;

public class OutstandingRow {
    private Long customerId;
    private String customerName;
    // =========================================
    // INVOICE INFO
    // =========================================

    private Long id;
    private String invoiceNumber;
    private Double totalAmount;
    private Double paidAmount;
    private Double outstandingAmount;

     public OutstandingRow() {
    }
     public OutstandingRow(
            Long id,
            String invoiceNumber,
            String customerName,
            Double totalAmount,
            Double paidAmount
    ) {

        this.id = id;

        this.invoiceNumber = invoiceNumber;

        this.customerName = customerName;

        this.totalAmount = totalAmount;

        this.paidAmount = paidAmount;

        this.outstandingAmount =
                (totalAmount != null ? totalAmount : 0)
                -
                (paidAmount != null ? paidAmount : 0);
    }

    // =========================================
    // OUTSTANDING REPORT CONSTRUCTOR
    // =========================================

    public OutstandingRow(
            Long customerId,
            String customerName,
            Double outstandingAmount
    ) {

        this.customerId = customerId;

        this.customerName = customerName;

        this.outstandingAmount = outstandingAmount;
    }

    // =========================================
    // GETTERS
    // =========================================

    public Long getCustomerId() {
        return customerId;
    }

    public Long getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    // =========================================
    // SETTERS
    // =========================================

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }
}