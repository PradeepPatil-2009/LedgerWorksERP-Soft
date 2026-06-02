package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Bom;
import com.ledger.ledgerworks.service.BomService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bom")
@CrossOrigin(origins = "http://localhost:3000")
public class BomController {

    private final BomService service;

    public BomController(
            BomService service
    ) {

        this.service = service;
    }

    // =====================================================
    // CREATE BOM
    // =====================================================

    @PostMapping
    public Bom create(
            @RequestBody
            Bom bom
    ) {

        return service.save(bom);
    }

    // =====================================================
    // GET ALL BOM
    // =====================================================

    @GetMapping
    public List<Bom> getAll() {

        return service.getAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public Bom getById(
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

        return "BOM deleted successfully";
    }
    @GetMapping("/by-fg/{itemId}")
    public Bom getByFinishedGood(
            @PathVariable Long itemId
    ) {

        return service.getByFinishedGood(itemId);
    }
}