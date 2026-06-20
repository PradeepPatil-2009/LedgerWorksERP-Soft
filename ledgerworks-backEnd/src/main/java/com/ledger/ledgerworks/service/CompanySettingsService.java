package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.repository.CompanySettingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanySettingsService {

    // Fixed primary key for the single settings record.
    private static final Long SETTINGS_ID = 1L;

    @Autowired
    private CompanySettingsRepository repo;

    // =====================================================
    // GET SETTINGS (single row, or a fresh empty one)
    // =====================================================

    public CompanySettings getSettings() {

        return repo.findById(SETTINGS_ID)
                .orElseGet(() -> {

                    CompanySettings empty = new CompanySettings();

                    empty.setId(SETTINGS_ID);

                    return empty;
                });
    }

    // =====================================================
    // SAVE SETTINGS (upsert id = 1)
    // =====================================================

    public CompanySettings saveSettings(
            CompanySettings settings
    ) {

        // Always force the single-record id so we upsert one row.
        settings.setId(SETTINGS_ID);

        return repo.save(settings);
    }
}
