package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.PaymentVoucher;
import com.ledger.ledgerworks.service.PaymentVoucherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-vouchers")
public class PaymentVoucherController {

    private final PaymentVoucherService service;

    public PaymentVoucherController(PaymentVoucherService service) {
        this.service = service;
    }

    // ✅ GET ALL
    @GetMapping
    public List<PaymentVoucher> getAll() {
        return service.findAll();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public PaymentVoucher getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ✅ CREATE
    @PostMapping
    public PaymentVoucher create(@RequestBody PaymentVoucher voucher) {
        return service.save(voucher);
    }
}
