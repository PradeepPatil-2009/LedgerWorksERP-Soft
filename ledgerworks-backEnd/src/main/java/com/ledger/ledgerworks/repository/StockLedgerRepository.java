package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.StockLedger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLedgerRepository
        extends JpaRepository<StockLedger, Long> {

    List<StockLedger> findByItemNameOrderByIdDesc(
            String itemName
    );
}