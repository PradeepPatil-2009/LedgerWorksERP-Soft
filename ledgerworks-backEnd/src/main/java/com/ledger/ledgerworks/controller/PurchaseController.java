package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.service.PurchaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:3000")
public class PurchaseController {
    @Autowired
    private PurchaseService service;

    // ================= CREATE =================

    @PostMapping
    public Purchase create(
            @RequestBody Purchase purchase
    ) {

        return service.create(purchase);
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public Purchase update(
            @PathVariable Long id,
            @RequestBody Purchase purchase
    ) {

        return service.update(id, purchase);
    }

    // ================= CANCEL =================

    @PostMapping("/cancel/{id}")
    public Purchase cancel(
            @PathVariable Long id
    ) {

        return service.cancel(id);
    }

    // ================= GET ALL =================

    @GetMapping
    public List<Purchase> getAll() {

        return service.getAll();
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public Purchase getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }
}