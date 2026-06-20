package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.DebitNote;
import com.ledger.ledgerworks.service.DebitNoteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debit-notes")
@CrossOrigin(origins = "http://localhost:3000")
public class DebitNoteController {

    private final DebitNoteService service;

    public DebitNoteController(DebitNoteService service) {
        this.service = service;
    }

    // ================= GET ALL =================

    @GetMapping
    public List<DebitNote> getAll() {
        return service.getAll();
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public DebitNote getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ================= CREATE =================

    @PostMapping
    public DebitNote create(@RequestBody DebitNote note) {
        return service.create(note);
    }
}
