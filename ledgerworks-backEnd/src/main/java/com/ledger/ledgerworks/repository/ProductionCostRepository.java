package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionCost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionCostRepository
        extends JpaRepository<ProductionCost, Long> {

    ProductionCost findByProductionEntryId(
            Long productionId
    );
}