package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.CashFlowResponse;
import com.ledger.ledgerworks.service.CashFlowService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class CashFlowController {

    private final CashFlowService service;

    public CashFlowController(CashFlowService service) {
        this.service = service;
    }

    @GetMapping("/cash-flow")
    public CashFlowResponse getCashFlow(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return service.generateCashFlow(from, to);
    }
}