package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String purchaseNumber;

    private LocalDate purchaseDate;

    private String status;

    // ================= VENDOR =================

    private String vendorName;

    @Column(length = 1000)
    private String vendorAddress;

    private String vendorGST;

    private String vendorPhone;

    private String vendorEmail;

    // ================= GST =================

    private String placeOfSupply;

    // ================= TOTALS =================

    private BigDecimal totalTaxable;

    private BigDecimal totalCGST;

    private BigDecimal totalSGST;

    private BigDecimal totalIGST;

    private BigDecimal grandTotal;

    // ================= NOTES =================

    @Column(length = 2000)
    private String remarks;

    // ================= ITEMS =================

    @OneToMany(
            mappedBy = "purchase",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PurchaseItem> items = new ArrayList<>();

    // ================= GETTERS SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorGST() {
        return vendorGST;
    }

    public void setVendorGST(String vendorGST) {
        this.vendorGST = vendorGST;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getPlaceOfSupply() {
        return placeOfSupply;
    }

    public void setPlaceOfSupply(String placeOfSupply) {
        this.placeOfSupply = placeOfSupply;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }
}