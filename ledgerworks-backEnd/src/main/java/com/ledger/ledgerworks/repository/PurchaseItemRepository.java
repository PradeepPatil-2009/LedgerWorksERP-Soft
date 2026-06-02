package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.PurchaseItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemRepository
        extends JpaRepository<PurchaseItem, Long> {

}