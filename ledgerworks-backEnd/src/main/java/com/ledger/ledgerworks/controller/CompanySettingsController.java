package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.service.CompanySettingsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-settings")
public class CompanySettingsController {

    @Autowired
    private CompanySettingsService service;

    // ================= GET CURRENT =================

    @GetMapping
    public CompanySettings getSettings() {

        return service.getSettings();
    }

    // ================= SAVE (PUT) =================

    @PutMapping
    public CompanySettings update(
            @RequestBody CompanySettings settings
    ) {

        return service.saveSettings(settings);
    }

    // ================= SAVE (POST) =================

    @PostMapping
    public CompanySettings save(
            @RequestBody CompanySettings settings
    ) {

        return service.saveSettings(settings);
    }
}
