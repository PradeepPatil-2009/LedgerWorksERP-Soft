package com.ledger.ledgerworks.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "production_raw_material")
public class ProductionRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // PRODUCTION ENTRY
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "production_entry_id")
    @JsonBackReference
    private ProductionEntry productionEntry;

    // =====================================================
    // RAW MATERIAL ITEM
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties({
        "productionOutputs",
        "productionRawMaterials",
        "bomItems",
        "hibernateLazyInitializer",
        "handler"
})
    private ItemMaster item;

    // =====================================================
    // QUANTITY CONSUMED
    // =====================================================

    @Column(nullable = false)
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
    // TOTAL AMOUNT
    // =====================================================

    private BigDecimal amount;

    // =====================================================
    // REMARKS
    // =====================================================

    private String remarks;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public ProductionEntry getProductionEntry() {
        return productionEntry;
    }

    public void setProductionEntry(
            ProductionEntry productionEntry
    ) {
        this.productionEntry = productionEntry;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks
    ) {
        this.remarks = remarks;
    }
}