package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.FinancialYear;
import com.ledger.ledgerworks.service.FinancialYearService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial-years")
public class FinancialYearController {

    private final FinancialYearService service;

    public FinancialYearController(FinancialYearService service) {
        this.service = service;
    }

    @GetMapping
    public List<FinancialYear> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public FinancialYear getActive() {
        return service.getActive();
    }

    @PostMapping
    public FinancialYear create(@RequestBody FinancialYear fy) {
        return service.create(fy);
    }

    @PutMapping("/{id}")
    public FinancialYear update(@PathVariable Long id, @RequestBody FinancialYear fy) {
        return service.update(id, fy);
    }

    @PutMapping("/{id}/activate")
    public FinancialYear activate(@PathVariable Long id) {
        return service.setActive(id);
    }

    @PutMapping("/{id}/lock")
    public FinancialYear lock(@PathVariable Long id) {
        return service.lock(id);
    }
}
