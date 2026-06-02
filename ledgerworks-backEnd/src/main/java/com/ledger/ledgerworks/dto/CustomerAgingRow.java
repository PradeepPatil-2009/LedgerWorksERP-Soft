package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;

public class CustomerAgingRow {

    private String customerName;
    private BigDecimal bucket0To30;
    private BigDecimal bucket31To60;
    private BigDecimal bucket61To90;
    private BigDecimal bucket90Plus;
    private BigDecimal totalOutstanding;

    public CustomerAgingRow(String customerName,
                             BigDecimal bucket0To30,
                             BigDecimal bucket31To60,
                             BigDecimal bucket61To90,
                             BigDecimal bucket90Plus,
                             BigDecimal totalOutstanding) {

        this.customerName = customerName;
        this.bucket0To30 = bucket0To30;
        this.bucket31To60 = bucket31To60;
        this.bucket61To90 = bucket61To90;
        this.bucket90Plus = bucket90Plus;
        this.totalOutstanding = totalOutstanding;
    }

    public String getCustomerName() { return customerName; }
    public BigDecimal getBucket0To30() { return bucket0To30; }
    public BigDecimal getBucket31To60() { return bucket31To60; }
    public BigDecimal getBucket61To90() { return bucket61To90; }
    public BigDecimal getBucket90Plus() { return bucket90Plus; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
}