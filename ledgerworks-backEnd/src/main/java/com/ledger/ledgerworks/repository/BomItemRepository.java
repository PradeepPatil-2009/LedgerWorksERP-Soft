package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.BomItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BomItemRepository
        extends JpaRepository<BomItem, Long> {
}