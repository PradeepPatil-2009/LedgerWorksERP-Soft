package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.NumberSeries;
import com.ledger.ledgerworks.service.NumberSeriesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/number-series")
public class NumberSeriesController {

    private final NumberSeriesService service;

    public NumberSeriesController(NumberSeriesService service) {
        this.service = service;
    }

    @GetMapping
    public List<NumberSeries> getAll() {
        return service.getAll();
    }

    @PostMapping
    public NumberSeries create(@RequestBody NumberSeries series) {
        return service.upsert(series);
    }

    @PutMapping
    public NumberSeries upsert(@RequestBody NumberSeries series) {
        return service.upsert(series);
    }
}
