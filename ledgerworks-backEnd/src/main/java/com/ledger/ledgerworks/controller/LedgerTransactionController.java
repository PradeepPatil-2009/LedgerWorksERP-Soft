package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.ApiResponse;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.service.LedgerTransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ledger-transactions")
public class LedgerTransactionController {

    private final LedgerTransactionService service;

    public LedgerTransactionController(LedgerTransactionService service) {
        this.service = service;
    }

    // ADMIN + ACCOUNTANT can post transaction
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    @PostMapping
    public ApiResponse postTransaction(@RequestBody LedgerTransaction transaction) {

        LedgerTransaction saved = service.postTransaction(transaction);

        return new ApiResponse("SUCCESS",
                "Transaction posted successfully",
                saved);
    }

    // ADMIN + ACCOUNTANT can reverse
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    @PostMapping("/reverse/{id}")
    public ApiResponse reverseTransaction(@PathVariable Long id) {

        service.reverseTransaction(id);

        return new ApiResponse("SUCCESS",
                "Transaction reversed successfully",
                null);
    }
}