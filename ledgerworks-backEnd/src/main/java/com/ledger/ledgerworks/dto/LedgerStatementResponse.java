package com.ledger.ledgerworks.dto;

import java.util.List;

public class LedgerStatementResponse {

    private String accountName;
    private List<LedgerStatementRow> rows;

    public LedgerStatementResponse(String accountName,
                                   List<LedgerStatementRow> rows) {
        this.accountName = accountName;
        this.rows = rows;
    }

    public String getAccountName() {
        return accountName;
    }

    public List<LedgerStatementRow> getRows() {
        return rows;
    }
}
