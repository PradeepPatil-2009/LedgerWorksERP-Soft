package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.CreditNote;
import com.ledger.ledgerworks.service.CreditNoteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-notes")
public class CreditNoteController {

    private final CreditNoteService service;

    public CreditNoteController(CreditNoteService service) {
        this.service = service;
    }

    // ================= GET ALL =================

    @GetMapping
    public List<CreditNote> getAll() {
        return service.getAll();
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public CreditNote getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ================= CREATE =================

    @PostMapping
    public CreditNote create(@RequestBody CreditNote note) {
        return service.create(note);
    }
}
