package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class ProductionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // PRODUCTION NUMBER
    // =====================================================

    @Column(unique = true)
    private String productionNumber;

    // =====================================================
    // DATE
    // =====================================================

    private LocalDate productionDate;

    // =====================================================
    // REMARKS
    // =====================================================

    private String remarks;

    // =====================================================
    // RAW MATERIAL CONSUMPTION
    // =====================================================

    @OneToMany(
            mappedBy = "productionEntry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<ProductionRawMaterial> rawMaterials =
            new ArrayList<>();

    // =====================================================
    // FINISHED GOODS
    // =====================================================

    @OneToMany(
            mappedBy = "productionEntry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<ProductionOutput> outputs =
            new ArrayList<>();

    // =====================================================
    // SCRAP
    // =====================================================

    @OneToMany(
            mappedBy = "productionEntry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<ProductionScrap> scraps =
            new ArrayList<>();

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(
            String productionNumber
    ) {
        this.productionNumber = productionNumber;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(
            LocalDate productionDate
    ) {
        this.productionDate = productionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks
    ) {
        this.remarks = remarks;
    }

    public List<ProductionRawMaterial> getRawMaterials() {
        return rawMaterials;
    }

    public void setRawMaterials(
            List<ProductionRawMaterial> rawMaterials
    ) {
        this.rawMaterials = rawMaterials;
    }

    public List<ProductionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(
            List<ProductionOutput> outputs
    ) {
        this.outputs = outputs;
    }

    public List<ProductionScrap> getScraps() {
        return scraps;
    }

    public void setScraps(
            List<ProductionScrap> scraps
    ) {
        this.scraps = scraps;
    }
}