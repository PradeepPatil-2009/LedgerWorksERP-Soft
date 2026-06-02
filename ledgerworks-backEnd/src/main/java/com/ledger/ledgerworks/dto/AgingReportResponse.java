package com.ledger.ledgerworks.dto;

import java.math.BigDecimal;
import java.util.List;

public class AgingReportResponse {

    private List<AgingReportRow> rows;

    private BigDecimal bucket0to30;
    private BigDecimal bucket31to60;
    private BigDecimal bucket61to90;
    private BigDecimal bucket90plus;

    public AgingReportResponse(List<AgingReportRow> rows,
                               BigDecimal bucket0to30,
                               BigDecimal bucket31to60,
                               BigDecimal bucket61to90,
                               BigDecimal bucket90plus) {
        this.rows = rows;
        this.bucket0to30 = bucket0to30;
        this.bucket31to60 = bucket31to60;
        this.bucket61to90 = bucket61to90;
        this.bucket90plus = bucket90plus;
    }

    public List<AgingReportRow> getRows() {
        return rows;
    }

    public BigDecimal getBucket0to30() {
        return bucket0to30;
    }

    public BigDecimal getBucket31to60() {
        return bucket31to60;
    }

    public BigDecimal getBucket61to90() {
        return bucket61to90;
    }

    public BigDecimal getBucket90plus() {
        return bucket90plus;
    }
}