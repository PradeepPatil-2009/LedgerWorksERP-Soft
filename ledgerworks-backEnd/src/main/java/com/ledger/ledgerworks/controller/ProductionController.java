package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.ProductionEntry;
import com.ledger.ledgerworks.service.ProductionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    private final ProductionService service;

    public ProductionController(
            ProductionService service
    ) {

        this.service = service;
    }

    // =====================================================
    // CREATE PRODUCTION
    // =====================================================

    @PostMapping
    public ProductionEntry create(

            @RequestBody
            ProductionEntry entry
    ) {

        return service.save(entry);
    }

    // =====================================================
    // GET ALL PRODUCTIONS
    // =====================================================

    @GetMapping
    public List<ProductionEntry> getAll() {

        return service.getAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ProductionEntry getById(

            @PathVariable
            Long id
    ) {

        return service.getById(id);
    }

    // =====================================================
    // DELETE PRODUCTION
    // =====================================================

    @DeleteMapping("/{id}")
    public String delete(

            @PathVariable
            Long id
    ) {

        service.delete(id);

        return "Production deleted successfully";
    }
}