package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.service.TrialBalanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class TrialBalanceController {

    private final TrialBalanceService service;

    // ✅ Constructor Injection (Best Practice)
    public TrialBalanceController(TrialBalanceService service) {
        this.service = service;
    }

    /**
     * Trial Balance Report
     *
     * Without Date Filter:
     * http://localhost:8080/reports/trial-balance
     *
     * With Date Filter:
     * http://localhost:8080/reports/trial-balance?from=2026-01-01&to=2026-12-31
     */
    @GetMapping("/trial-balance")
    public TrialBalanceResponse getTrialBalance(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return service.generateTrialBalance(from, to);
    }
}