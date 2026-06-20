package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.ReceiptVoucher;
import com.ledger.ledgerworks.service.ReceiptVoucherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipt-vouchers")
public class ReceiptVoucherController {

    private final ReceiptVoucherService service;

    public ReceiptVoucherController(ReceiptVoucherService service) {
        this.service = service;
    }

    // ✅ GET ALL
    @GetMapping
    public List<ReceiptVoucher> getAll() {
        return service.findAll();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ReceiptVoucher getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ✅ CREATE
    @PostMapping
    public ReceiptVoucher create(@RequestBody ReceiptVoucher voucher) {
        return service.save(voucher);
    }
}
