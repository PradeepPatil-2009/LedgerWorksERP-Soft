package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionScrap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionScrapRepository
        extends JpaRepository<ProductionScrap, Long> {

    // ================= GET BY PRODUCTION =================

    List<ProductionScrap>
    findByProductionEntryId(Long productionId);
}