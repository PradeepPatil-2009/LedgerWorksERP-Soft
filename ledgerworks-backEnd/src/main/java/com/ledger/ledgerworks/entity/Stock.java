package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String hsnCode;

    private BigDecimal availableQty;

    private BigDecimal purchaseQty;

    private BigDecimal salesQty;

    private BigDecimal lastPurchaseRate;

    private BigDecimal lastSalesRate;

    private String unit;

    private String status;

    // ================= GETTERS SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public BigDecimal getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(BigDecimal availableQty) {
        this.availableQty = availableQty;
    }

    public BigDecimal getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(BigDecimal purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public BigDecimal getSalesQty() {
        return salesQty;
    }

    public void setSalesQty(BigDecimal salesQty) {
        this.salesQty = salesQty;
    }

    public BigDecimal getLastPurchaseRate() {
        return lastPurchaseRate;
    }

    public void setLastPurchaseRate(BigDecimal lastPurchaseRate) {
        this.lastPurchaseRate = lastPurchaseRate;
    }

    public BigDecimal getLastSalesRate() {
        return lastSalesRate;
    }

    public void setLastSalesRate(BigDecimal lastSalesRate) {
        this.lastSalesRate = lastSalesRate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}