package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "bom")
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // FINISHED GOOD
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "finished_good_id")
    @JsonIgnoreProperties({
            "productionOutputs",
            "productionRawMaterials",
            "bomItems",
            "hibernateLazyInitializer",
            "handler"
    })    
    private ItemMaster finishedGood;

    // =====================================================
    // BOM NAME
    // =====================================================

    private String bomName;

    // =====================================================
    // STATUS
    // =====================================================

    private String status;

    // =====================================================
    // BOM ITEMS
    // =====================================================

    @OneToMany(
            mappedBy = "bom",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<BomItem> items;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public ItemMaster getFinishedGood() {
        return finishedGood;
    }

    public void setFinishedGood(
            ItemMaster finishedGood
    ) {
        this.finishedGood = finishedGood;
    }

    public String getBomName() {
        return bomName;
    }

    public void setBomName(
            String bomName
    ) {
        this.bomName = bomName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }

    public List<BomItem> getItems() {
        return items;
    }

    public void setItems(
            List<BomItem> items
    ) {
        this.items = items;
    }
}