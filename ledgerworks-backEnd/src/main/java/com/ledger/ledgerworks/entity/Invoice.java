package com.ledger.ledgerworks.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC =================

    @Column(unique = true)
    private String invoiceNumber;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private String customerName;

    private String customerAddress;

    private String customerPhone;

    private String customerEmail;

    private String customerState;
    private String customerGSTNumber;
    private String customerStateCode;
    
    @Column(length = 2000)
    private String descriptions;

    public String getCustomerGSTNumber() {
		return customerGSTNumber;
	}

	public void setCustomerGSTNumber(String customerGSTNumber) {
		this.customerGSTNumber = customerGSTNumber;
	}

	public String getCustomerStateCode() {
		return customerStateCode;
	}

	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}

	

    private String paymentStatus = "PENDING";

    private String paidStatus = "UNPAID";

    private BigDecimal paidAmount = BigDecimal.ZERO;

    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    private BigDecimal totalTaxable = BigDecimal.ZERO;

    private BigDecimal totalCGST = BigDecimal.ZERO;

    private BigDecimal totalSGST = BigDecimal.ZERO;

    private BigDecimal totalIGST = BigDecimal.ZERO;

    private BigDecimal grandTotal = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ================= CUSTOMER =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")

    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Customer customer;

    // ================= ITEMS =================

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @JsonIgnoreProperties("invoice")
    private List<InvoiceItem> items = new ArrayList<>();

    // ================= DELIVERY CHALLAN =================

    @OneToOne(mappedBy = "invoice")

    @JsonIgnoreProperties({
            "items",
            "invoice"
    })
    private DeliveryChallan deliveryChallan;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getTotalTaxable() {
        return totalTaxable;
    }

    public void setTotalTaxable(BigDecimal totalTaxable) {
        this.totalTaxable = totalTaxable;
    }

    public BigDecimal getTotalCGST() {
        return totalCGST;
    }

    public void setTotalCGST(BigDecimal totalCGST) {
        this.totalCGST = totalCGST;
    }

    public BigDecimal getTotalSGST() {
        return totalSGST;
    }

    public void setTotalSGST(BigDecimal totalSGST) {
        this.totalSGST = totalSGST;
    }

    public BigDecimal getTotalIGST() {
        return totalIGST;
    }

    public void setTotalIGST(BigDecimal totalIGST) {
        this.totalIGST = totalIGST;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public DeliveryChallan getDeliveryChallan() {
        return deliveryChallan;
    }

    public void setDeliveryChallan(
            DeliveryChallan deliveryChallan
    ) {
        this.deliveryChallan = deliveryChallan;
    }

	
}