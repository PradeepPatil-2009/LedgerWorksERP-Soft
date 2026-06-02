package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionConsumption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionConsumptionRepository
        extends JpaRepository<ProductionConsumption, Long> {

    // ================= GET BY PRODUCTION =================

    List<ProductionConsumption>
    findByProductionEntryId(Long productionId);
}