package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "production_cost")
public class ProductionCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // PRODUCTION ENTRY
    // =====================================================

    @OneToOne
    @JoinColumn(name = "production_entry_id")
    private ProductionEntry productionEntry;

    // =====================================================
    // COSTING DATE
    // =====================================================

    private LocalDate costingDate;

    // =====================================================
    // RAW MATERIAL COST
    // =====================================================

    private BigDecimal materialCost;

    // =====================================================
    // LABOUR COST
    // =====================================================

    private BigDecimal labourCost;

    // =====================================================
    // OVERHEAD COST
    // =====================================================

    private BigDecimal overheadCost;

    // =====================================================
    // TOTAL PRODUCTION COST
    // =====================================================

    private BigDecimal totalCost;

    // =====================================================
    // PRODUCED QUANTITY
    // =====================================================

    private BigDecimal productionQty;

    // =====================================================
    // COST PER UNIT
    // =====================================================

    private BigDecimal costPerUnit;

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

    public LocalDate getCostingDate() {
        return costingDate;
    }

    public void setCostingDate(
            LocalDate costingDate
    ) {
        this.costingDate = costingDate;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(
            BigDecimal materialCost
    ) {
        this.materialCost = materialCost;
    }

    public BigDecimal getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(
            BigDecimal labourCost
    ) {
        this.labourCost = labourCost;
    }

    public BigDecimal getOverheadCost() {
        return overheadCost;
    }

    public void setOverheadCost(
            BigDecimal overheadCost
    ) {
        this.overheadCost = overheadCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(
            BigDecimal totalCost
    ) {
        this.totalCost = totalCost;
    }

    public BigDecimal getProductionQty() {
        return productionQty;
    }

    public void setProductionQty(
            BigDecimal productionQty
    ) {
        this.productionQty = productionQty;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(
            BigDecimal costPerUnit
    ) {
        this.costPerUnit = costPerUnit;
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