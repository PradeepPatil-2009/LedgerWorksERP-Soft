package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.service.ItemMasterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemMasterController {

    @Autowired
    private ItemMasterService service;

    // ================= CREATE =================

    @PostMapping
    public ItemMaster create(
            @RequestBody ItemMaster item
    ) {

        return service.create(item);
    }

    // ================= GET ALL =================

    @GetMapping
    public List<ItemMaster> getAll() {

        return service.getAll();
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ItemMaster getById(
            @PathVariable Long id
    ) {

        return service.getById(id);
    }
}