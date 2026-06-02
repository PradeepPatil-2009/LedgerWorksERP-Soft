package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.GstSummaryDto;
import com.ledger.ledgerworks.dto.GstDetailDto;
import com.ledger.ledgerworks.dto.MonthlySummary;

import com.ledger.ledgerworks.repository.InvoiceRepository;

import com.ledger.ledgerworks.service.GstReportService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gst")
@CrossOrigin(origins = "http://localhost:3000")
public class GstReportController {

    @Autowired
    private GstReportService service;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // ================= SUMMARY =================

    @GetMapping("/summary")
    public GstSummaryDto getSummary(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate
    ) {

        return service.getSummary(
                fromDate,
                toDate
        );
    }

    // ================= DETAILS =================

    @GetMapping("/details")
    public List<GstDetailDto> getDetails(

            @RequestParam String fromDate,

            @RequestParam String toDate
    ) {

        return service.getDetailedReport(

                LocalDate.parse(fromDate),

                LocalDate.parse(toDate)
        );
    }

    // ================= MONTHLY SALES =================
 // ================= MONTHLY SALES =================

    @GetMapping("/monthly-sales")
    public List<Map<String, Object>> monthlySales() {

        List<Object[]> rows =
                invoiceRepository.getMonthlySales();

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, Object> map =
                    new HashMap<>();

            map.put("month", row[0]);

            map.put("sales", row[1]);

            result.add(map);
        }

        return result;
    }
}