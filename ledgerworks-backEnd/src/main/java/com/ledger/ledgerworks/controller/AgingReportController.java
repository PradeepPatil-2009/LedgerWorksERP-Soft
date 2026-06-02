package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.CustomerAgingRow;
import com.ledger.ledgerworks.service.AgingReportService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class AgingReportController {

    private final AgingReportService service;

    public AgingReportController(
            AgingReportService service
    ) {
        this.service = service;
    }

    @GetMapping("/customer-aging")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public List<CustomerAgingRow>
    getCustomerWiseAging() {

        return service.generateCustomerWiseAging();
    }
}