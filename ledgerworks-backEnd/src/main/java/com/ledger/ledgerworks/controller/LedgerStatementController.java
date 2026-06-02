package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.LedgerStatementRow;
import com.ledger.ledgerworks.service.LedgerStatementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ledger-statement")
public class LedgerStatementController {

    private final LedgerStatementService ledgerStatementService;

    public LedgerStatementController(LedgerStatementService ledgerStatementService) {
        this.ledgerStatementService = ledgerStatementService;
    }

    @GetMapping("/{accountId}")
    public List<LedgerStatementRow> getStatement(
            @PathVariable Long accountId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return ledgerStatementService.getStatement(accountId, from, to);
    }
}