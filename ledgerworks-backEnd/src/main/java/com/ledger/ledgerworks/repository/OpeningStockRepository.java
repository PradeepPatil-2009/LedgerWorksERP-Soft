package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.OpeningStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpeningStockRepository
        extends JpaRepository<OpeningStock, Long> {

    List<OpeningStock> findByItemNameOrderByIdDesc(
            String itemName
    );
}
