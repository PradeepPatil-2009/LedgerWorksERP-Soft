package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.ProfitLossResponse;
import com.ledger.ledgerworks.service.ProfitLossService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")   // ✅ FIXED (IMPORTANT)
public class ProfitLossController {

    private final ProfitLossService profitLossService;

    public ProfitLossController(ProfitLossService profitLossService) {
        this.profitLossService = profitLossService;
    }

    @GetMapping("/profit-loss")
    public ProfitLossResponse getProfitLoss(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return profitLossService.generateProfitLoss(from, to);
    }
}