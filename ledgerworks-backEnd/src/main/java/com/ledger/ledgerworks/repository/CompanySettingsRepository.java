package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.CompanySettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySettingsRepository
        extends JpaRepository<CompanySettings, Long> {
}
