package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}