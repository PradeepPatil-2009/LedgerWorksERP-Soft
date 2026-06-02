package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository
        extends JpaRepository<Purchase, Long> {

    Purchase findTopByOrderByIdDesc();

}