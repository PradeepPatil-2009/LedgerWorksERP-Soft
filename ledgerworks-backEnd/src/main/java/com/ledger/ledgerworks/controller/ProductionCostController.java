package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.service.ProductionCostService;
import com.ledger.ledgerworks.service.ProductionCostService.ProductionCostSummary;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-cost")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductionCostController {

    private final ProductionCostService service;

    public ProductionCostController(
            ProductionCostService service
    ) {

        this.service = service;
    }

    // =====================================================
    // CALCULATE COST
    // =====================================================

    @PostMapping("/calculate/{productionId}")
    public ProductionCostSummary calculate(

            @PathVariable
            Long productionId
    ) {

        return service.calculateCost(
                productionId
        );
    }

    // =====================================================
    // GET COST
    // =====================================================

    @GetMapping("/{productionId}")
    public ProductionCostSummary get(

            @PathVariable
            Long productionId
    ) {

        return service.getByProduction(
                productionId
        );
    }
}