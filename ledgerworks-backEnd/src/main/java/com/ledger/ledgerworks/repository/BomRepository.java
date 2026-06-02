package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.Bom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BomRepository
        extends JpaRepository<Bom, Long> {

    Optional<Bom> findByFinishedGoodId(
            Long itemId
    );
    
}