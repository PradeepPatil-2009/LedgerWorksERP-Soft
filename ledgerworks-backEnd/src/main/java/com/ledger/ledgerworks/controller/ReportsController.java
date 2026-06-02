package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.service.TrialBalanceService;
import com.ledger.ledgerworks.service.ProfitLossService;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.dto.ProfitLossResponse;
import com.ledger.ledgerworks.dto.TrialBalanceResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final TrialBalanceService trialBalanceService;
    private final ProfitLossService profitLossService;

    public ReportsController(
            TrialBalanceService trialBalanceService,
            ProfitLossService profitLossService) {

        this.trialBalanceService = trialBalanceService;
        this.profitLossService = profitLossService;
    }

    // ================= TRIAL BALANCE =================

 /*   @GetMapping("/trial-balance")
    public TrialBalanceResponse getTrialBalance(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return trialBalanceService.generateTrialBalance(fromDate, toDate);
    }
    */

    // ================= PROFIT & LOSS =================

  /*  @GetMapping("/profit-loss")
    public ProfitLossResponse getProfitLoss(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return profitLossService.generateProfitLoss(fromDate, toDate);
    }*/

}