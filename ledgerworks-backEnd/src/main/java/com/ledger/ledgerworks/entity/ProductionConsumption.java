package com.ledger.ledgerworks.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class ProductionConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= PRODUCTION ENTRY =================

    @ManyToOne
    @JoinColumn(name = "production_id")
    private ProductionEntry productionEntry;

    // ================= RAW MATERIAL ITEM =================

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemMaster item;

    // ================= QUANTITY =================

    private BigDecimal  quantity;

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

    
}