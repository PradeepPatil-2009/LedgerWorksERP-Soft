package com.ledger.ledgerworks.dto;

public class GstStateResponse {

    private String code;
    private String stateName;

    public GstStateResponse() {
    }

    public GstStateResponse(String code, String stateName) {
        this.code = code;
        this.stateName = stateName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
