package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.OutstandingRow;
import com.ledger.ledgerworks.service.OutstandingService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class OutstandingController {

    private final OutstandingService service;

    public OutstandingController(
            OutstandingService service
    ) {
        this.service = service;
    }

    @GetMapping("/outstanding")
    public List<OutstandingRow> getOutstanding() {

        return service.getOutstandingReport();
    }
}