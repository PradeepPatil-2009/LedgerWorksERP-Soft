package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionOutput;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionOutputRepository
        extends JpaRepository<ProductionOutput, Long> {

    // ================= GET BY PRODUCTION =================

    List<ProductionOutput>
    findByProductionEntryId(Long productionId);
}