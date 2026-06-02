package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.InvoicePaymentRequest;
import com.ledger.ledgerworks.service.InvoicePaymentService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice-payments")

public class InvoicePaymentController {

    private final InvoicePaymentService service;

    public InvoicePaymentController(
            InvoicePaymentService service
    ) {
        this.service = service;
    }

    // =========================================
    // SAVE PAYMENT
    // =========================================

    @PostMapping
    public String savePayment(
            @RequestBody InvoicePaymentRequest request
    ) {

        service.payInvoice(
                request.getInvoiceNumber(),
                request.getAmount(),
                request.getPaymentMode(),
                request.getReference()
        );

        return "Payment Saved Successfully";
    }
}