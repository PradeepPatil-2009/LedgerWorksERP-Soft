package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionEntryRepository
        extends JpaRepository<ProductionEntry, Long> {

    ProductionEntry findTopByOrderByIdDesc();

    ProductionEntry
    findTopByProductionNumberIsNotNullOrderByIdDesc();
}