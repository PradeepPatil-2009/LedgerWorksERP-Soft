package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ItemMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemMasterRepository
        extends JpaRepository<ItemMaster, Long> {

    // =========================================
    // FIND BY ITEM NAME
    // =========================================

    Optional<ItemMaster> findByItemName(
            String itemName
    );

    // =========================================
    // CHECK DUPLICATE ITEM
    // =========================================

    boolean existsByItemName(
            String itemName
    );
}