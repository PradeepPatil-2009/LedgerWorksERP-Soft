package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class MaterialIssueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // MATERIAL ISSUE
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "material_issue_id")
    
    @JsonBackReference
    private MaterialIssue materialIssue;

    // =====================================================
    // ITEM
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemMaster item;

    // =====================================================
    // QUANTITY
    // =====================================================

    private BigDecimal quantity;

    // =====================================================
    // UNIT
    // =====================================================

    private String unit;

    // =====================================================
    // RATE
    // =====================================================

    private BigDecimal rate;

    // =====================================================
    // AMOUNT
    // =====================================================

    private BigDecimal amount;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public MaterialIssue getMaterialIssue() {
        return materialIssue;
    }

    public void setMaterialIssue(
            MaterialIssue materialIssue
    ) {
        this.materialIssue = materialIssue;
    }

    public ItemMaster getItem() {
        return item;
    }

    public void setItem(
            ItemMaster item
    ) {
        this.item = item;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(
            BigDecimal quantity
    ) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(
            String unit
    ) {
        this.unit = unit;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(
            BigDecimal rate
    ) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }
}