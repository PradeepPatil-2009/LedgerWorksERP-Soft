package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.GstRate;
import com.ledger.ledgerworks.service.GstRateService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GST rate master. Any authenticated user may read the active slabs (to fill in
 * a rate on an invoice); only ADMIN may create/update/delete. The role rules are
 * enforced in {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/api/gst-rates")
public class GstRateController {

    private final GstRateService service;

    public GstRateController(GstRateService service) {
        this.service = service;
    }

    // ================= LIST ACTIVE =================

    @GetMapping
    public List<GstRate> list() {
        return service.listActive();
    }

    // ================= CREATE =================

    @PostMapping
    public GstRate create(@RequestBody GstRate rate) {
        return service.create(rate);
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public GstRate update(@PathVariable Long id, @RequestBody GstRate rate) {
        return service.update(id, rate);
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
