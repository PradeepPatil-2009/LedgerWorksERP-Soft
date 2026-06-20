package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.DebitNote;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitNoteRepository
        extends JpaRepository<DebitNote, Long> {

    DebitNote findTopByOrderByIdDesc();
}
