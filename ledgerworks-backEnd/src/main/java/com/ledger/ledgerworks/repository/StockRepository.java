package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository
        extends JpaRepository<Stock, Long> {

    Optional<Stock> findByItemName(
            String itemName
    );

    List<Stock> findByAvailableQtyLessThan(
            BigDecimal qty
    );
}