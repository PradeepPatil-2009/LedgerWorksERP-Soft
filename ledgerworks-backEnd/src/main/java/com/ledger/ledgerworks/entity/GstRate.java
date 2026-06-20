package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * GST rate slab master (e.g. 0 / 5 / 12 / 18 / 28%). Inactive rows are kept for
 * historical reference but hidden from the active-rate lookups used on forms.
 */
@Entity
@Table(name = "gst_rate")
public class GstRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal rate;

    private String label;

    private boolean active = true;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
