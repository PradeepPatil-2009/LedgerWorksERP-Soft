package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.StockLedger;
import com.ledger.ledgerworks.service.StockLedgerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-ledger")
public class StockLedgerController {

    @Autowired
    private StockLedgerService service;

    // =====================================================
    // GET ALL LEDGER ENTRIES
    // =====================================================

    @GetMapping
    public List<StockLedger> getAll() {

        return service.getAll();
    }

    // =====================================================
    // ITEM WISE LEDGER
    // =====================================================

    @GetMapping("/{itemName}")
    public List<StockLedger> getByItem(

            @PathVariable
            String itemName
    ) {

        return service.getByItem(itemName);
    }
}