package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Vendor;
import com.ledger.ledgerworks.service.VendorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vendor> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Vendor create(@RequestBody Vendor vendor) {
        return service.create(vendor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public Vendor update(@PathVariable Long id,@RequestBody Vendor vender) {
    		return service.update(id, vender);
    }
}