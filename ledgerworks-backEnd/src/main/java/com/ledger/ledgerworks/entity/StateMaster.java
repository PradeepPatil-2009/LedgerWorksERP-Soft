package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

/**
 * Master list of Indian states / union territories with their GST
 * numeric state codes (e.g. "27" for Maharashtra).
 */
@Entity
@Table(name = "state_master")
public class StateMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateName;

    // GST numeric state code, e.g. "27".
    private String stateCode;

    private boolean active = true;

    // ================= GETTERS SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
