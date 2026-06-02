package com.ledger.ledgerworks.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class ProductionScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= PRODUCTION ENTRY =================

    @ManyToOne
    @JoinColumn(name = "production_id")
    @JsonBackReference
    private ProductionEntry productionEntry;

    // ================= SCRAP ITEM =================

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemMaster item;

    // ================= SCRAP QUANTITY =================

    private BigDecimal  quantity;

    // ================= REMARKS =================

    private String remarks;

    // ================= GETTERS & SETTERS =================

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

    public void setItem(ItemMaster item) {
        this.item = item;
    }

    

    public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}