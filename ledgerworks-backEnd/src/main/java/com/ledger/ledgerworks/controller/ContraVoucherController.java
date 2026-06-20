package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.ContraVoucher;
import com.ledger.ledgerworks.service.ContraVoucherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contra-vouchers")
@CrossOrigin
public class ContraVoucherController {

    private final ContraVoucherService service;

    public ContraVoucherController(ContraVoucherService service) {
        this.service = service;
    }

    // ✅ GET ALL
    @GetMapping
    public List<ContraVoucher> getAll() {
        return service.findAll();
    }

    // ✅ CREATE
    @PostMapping
    public ContraVoucher create(@RequestBody ContraVoucher voucher) {
        return service.save(voucher);
    }
}
