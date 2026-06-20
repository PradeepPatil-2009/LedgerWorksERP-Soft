package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

/**
 * Single-record company configuration. The application always upserts the
 * row with id = 1 so there is exactly one settings record.
 */
@Entity
@Table(name = "company_settings")
public class CompanySettings {

    @Id
    private Long id;

    private String companyName;

    private String gstNumber;

    private String panNumber;

    @Column(length = 1000)
    private String address;

    private String bankName;

    private String accountNumber;

    private String ifsc;

    private String branch;

    @Column(length = 1000)
    private String footerMessage;

    // =========================================
    // LOGO (base64 data-URL string)
    // =========================================

    @Lob
    @Column(columnDefinition = "TEXT")
    private String logoBase64;

    // ================= GETTERS SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getFooterMessage() {
        return footerMessage;
    }

    public void setFooterMessage(String footerMessage) {
        this.footerMessage = footerMessage;
    }

    public String getLogoBase64() {
        return logoBase64;
    }

    public void setLogoBase64(String logoBase64) {
        this.logoBase64 = logoBase64;
    }
}
