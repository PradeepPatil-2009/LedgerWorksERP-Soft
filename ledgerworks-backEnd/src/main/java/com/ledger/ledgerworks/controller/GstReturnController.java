package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.Gstr1Row;
import com.ledger.ledgerworks.dto.Gstr3bSummary;
import com.ledger.ledgerworks.service.GstReturnService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-only GSTR return exports built from invoices.
 * Falls under the existing authenticated /api/gst security rules.
 */
@RestController
@RequestMapping("/api/gst")
public class GstReturnController {

    private final GstReturnService service;

    public GstReturnController(GstReturnService service) {
        this.service = service;
    }

    // ================= GSTR-1 =================

    @GetMapping("/gstr1")
    public List<Gstr1Row> gstr1(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return service.getGstr1(fromDate, toDate);
    }

    // ================= GSTR-3B =================

    @GetMapping("/gstr3b")
    public Gstr3bSummary gstr3b(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return service.getGstr3b(fromDate, toDate);
    }
}
