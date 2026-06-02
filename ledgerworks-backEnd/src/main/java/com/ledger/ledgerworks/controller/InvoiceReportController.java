package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.InvoiceOutstandingRow;
import com.ledger.ledgerworks.service.InvoiceReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class InvoiceReportController {

    private final InvoiceReportService service;

    public InvoiceReportController(InvoiceReportService service) {
        this.service = service;
    }

    @GetMapping("/invoice-outstanding")
    public List<InvoiceOutstandingRow> getInvoiceWiseOutstanding() {
        return service.getInvoiceWiseOutstanding();
    }
}