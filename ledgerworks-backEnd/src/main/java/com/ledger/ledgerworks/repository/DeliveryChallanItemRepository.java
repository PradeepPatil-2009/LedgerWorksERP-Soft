package com.ledger.ledgerworks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ledger.ledgerworks.entity.DeliveryChallanItem;

public interface DeliveryChallanItemRepository
        extends JpaRepository<DeliveryChallanItem, Long> {
}