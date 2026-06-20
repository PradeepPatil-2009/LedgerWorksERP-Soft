package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.CreditNote;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditNoteRepository
        extends JpaRepository<CreditNote, Long> {

    CreditNote findTopByOrderByIdDesc();
}
