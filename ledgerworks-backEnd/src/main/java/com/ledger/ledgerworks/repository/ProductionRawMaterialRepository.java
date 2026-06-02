package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ProductionRawMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRawMaterialRepository
        extends JpaRepository<
                ProductionRawMaterial,
                Long
        > {
}