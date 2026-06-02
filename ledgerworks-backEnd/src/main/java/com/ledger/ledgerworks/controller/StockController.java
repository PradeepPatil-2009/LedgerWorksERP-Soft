package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Stock;
import com.ledger.ledgerworks.service.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:3000")
public class StockController {

    @Autowired
    private StockService service;

    // =====================================================
    // GET ALL STOCKS
    // =====================================================

    @GetMapping
    public List<Stock> getAll() {

        return service.getAll();
    }

    // =====================================================
    // LOW STOCK REPORT
    // =====================================================

    @GetMapping("/low-stock")
    public List<Stock> getLowStock() {

        return service.getLowStock();
    }
}