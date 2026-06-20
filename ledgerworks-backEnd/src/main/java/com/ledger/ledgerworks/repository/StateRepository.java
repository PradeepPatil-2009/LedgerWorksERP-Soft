package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.StateMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository
        extends JpaRepository<StateMaster, Long> {

    // =========================================
    // ACTIVE STATES (ordered by GST code)
    // =========================================

    List<StateMaster> findByActiveTrueOrderByStateCodeAsc();
}
