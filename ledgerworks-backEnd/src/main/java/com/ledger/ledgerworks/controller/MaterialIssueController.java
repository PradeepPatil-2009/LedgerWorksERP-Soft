package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.MaterialIssue;
import com.ledger.ledgerworks.service.MaterialIssueService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material-issue")
public class MaterialIssueController {

    private final MaterialIssueService service;

    public MaterialIssueController(
            MaterialIssueService service
    ) {

        this.service = service;
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public MaterialIssue create(

            @RequestBody
            MaterialIssue issue
    ) {

        return service.create(issue);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public List<MaterialIssue> getAll() {

        return service.getAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public MaterialIssue getById(

            @PathVariable
            Long id
    ) {

        return service.getById(id);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public String delete(

            @PathVariable
            Long id
    ) {

        service.delete(id);

        return "Material Issue deleted successfully";
    }
}