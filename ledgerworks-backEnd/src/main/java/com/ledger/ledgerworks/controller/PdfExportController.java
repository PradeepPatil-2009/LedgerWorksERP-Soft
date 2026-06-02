package com.ledger.ledgerworks.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ledger.ledgerworks.service.PdfExportService;

@RestController
@RequestMapping("/reports")
public class PdfExportController {

    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping("/trial-balance/pdf")
    public ResponseEntity<byte[]> exportTrialBalancePdf(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        try {

            byte[] pdfBytes =
                    pdfExportService.generateTrialBalancePdf(from, to);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "inline; filename=trial-balance.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}