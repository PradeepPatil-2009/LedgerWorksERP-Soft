package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@Entity
@Table(name = "bom_item")
public class BomItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // BOM
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "bom_id")
    @JsonBackReference
    private Bom bom;

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
    // QUANTITY
    // =====================================================

    private BigDecimal quantity;

    // =====================================================
    // UNIT
    // =====================================================

    private String unit;

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

    public Bom getBom() {
        return bom;
    }

    public void setBom(
            Bom bom
    ) {
        this.bom = bom;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks
    ) {
        this.remarks = remarks;
    }
}