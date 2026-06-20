package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.OpeningStock;
import com.ledger.ledgerworks.service.OpeningStockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Opening stock endpoints. Records an item's opening quantity + value into the
 * stock ledger so reports start from the correct balance.
 */
@RestController
@RequestMapping("/api/opening-stock")
public class OpeningStockController {

    @Autowired
    private OpeningStockService service;

    // ================= GET ALL =================

    @GetMapping
    public List<OpeningStock> getAll() {

        return service.getAll();
    }

    // ================= CREATE SINGLE =================

    @PostMapping
    public OpeningStock create(
            @RequestBody OpeningStock entry
    ) {

        return service.record(entry);
    }

    // ================= CREATE BULK =================

    @PostMapping("/bulk")
    public List<OpeningStock> createBulk(
            @RequestBody List<OpeningStock> entries
    ) {

        return service.recordBulk(entries);
    }
}
