
package com.ledger.ledgerworks.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ledger.ledgerworks.dto.BalanceSheetResponse;
import com.ledger.ledgerworks.service.BalanceSheetService;

@RestController
@RequestMapping("/api/reports")
public class BalanceSheetController {

    @Autowired
    private BalanceSheetService service;

    @GetMapping("/balance-sheet")
    public BalanceSheetResponse getBalanceSheet(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        return service.generateBalanceSheet(from, to);
    }
}
