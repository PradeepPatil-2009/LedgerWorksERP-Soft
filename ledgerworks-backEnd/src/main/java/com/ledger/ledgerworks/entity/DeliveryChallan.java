package com.ledger.ledgerworks.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_challan")
public class DeliveryChallan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String challanNumber;

    private LocalDate challanDate;

    private String customerName;

    private String customerAddress;

    private String customerPhone;

    private String customerEmail;

    private String customerGST;

    private String placeOfSupply;

    private String descriptions;

    private String transportName;

    private String vehicleNumber;

    private String status = "ACTIVE";

    private Boolean invoiceCreated = false;

    private BigDecimal totalTaxable = BigDecimal.ZERO;

    private BigDecimal totalCGST = BigDecimal.ZERO;

    private BigDecimal totalSGST = BigDecimal.ZERO;

    private BigDecimal totalIGST = BigDecimal.ZERO;

    private BigDecimal grandTotal = BigDecimal.ZERO;

    // ================= ITEMS =================

    @OneToMany(
            mappedBy = "challan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @JsonIgnoreProperties("challan")
    private List<DeliveryChallanItem> items = new ArrayList<>();

    // ================= INVOICE =================

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    @JsonIgnoreProperties({
            "items",
            "deliveryChallan",
            "customer"
    })
    private Invoice invoice;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public LocalDate getChallanDate() {
        return challanDate;
    }

    public void setChallanDate(LocalDate challanDate) {
        this.challanDate = challanDate;
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

    public String getCustomerGST() {
        return customerGST;
    }

    public void setCustomerGST(String customerGST) {
        this.customerGST = customerGST;
    }

    public String getPlaceOfSupply() {
        return placeOfSupply;
    }

    public void setPlaceOfSupply(String placeOfSupply) {
        this.placeOfSupply = placeOfSupply;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getInvoiceCreated() {
        return invoiceCreated;
    }

    public void setInvoiceCreated(Boolean invoiceCreated) {
        this.invoiceCreated = invoiceCreated;
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

    public List<DeliveryChallanItem> getItems() {
        return items;
    }

    public void setItems(List<DeliveryChallanItem> items) {
        this.items = items;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}