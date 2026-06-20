package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.ImportResult;
import com.ledger.ledgerworks.service.ImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Bulk data import endpoints. Accept a multipart .xlsx or .csv file and
 * return a summary of imported / skipped rows. The service is defensive and
 * never throws on bad rows, so these endpoints do not 500 on malformed input.
 */
@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    // ================= CUSTOMERS =================

    @PostMapping("/customers")
    public ImportResult importCustomers(
            @RequestParam("file") MultipartFile file
    ) {

        return importService.importCustomers(file);
    }

    // ================= VENDORS =================

    @PostMapping("/vendors")
    public ImportResult importVendors(
            @RequestParam("file") MultipartFile file
    ) {

        return importService.importVendors(file);
    }

    // ================= ITEMS =================

    @PostMapping("/items")
    public ImportResult importItems(
            @RequestParam("file") MultipartFile file
    ) {

        return importService.importItems(file);
    }
}
