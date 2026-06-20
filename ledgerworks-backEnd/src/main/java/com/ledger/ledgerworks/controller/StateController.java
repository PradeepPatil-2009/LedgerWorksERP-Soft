package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.StateMaster;
import com.ledger.ledgerworks.service.StateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/states")
public class StateController {

    @Autowired
    private StateService service;

    // ================= GET ALL (ACTIVE) =================

    @GetMapping
    public List<StateMaster> getAll() {

        return service.getAllActive();
    }

    // ================= CREATE =================

    @PostMapping
    public StateMaster create(
            @RequestBody StateMaster state
    ) {

        return service.create(state);
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public StateMaster update(
            @PathVariable Long id,
            @RequestBody StateMaster state
    ) {

        return service.update(id, state);
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        service.delete(id);
    }
}
