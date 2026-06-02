package com.ledger.ledgerworks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.ledgerworks.entity.DeliveryChallan;

public interface DeliveryChallanRepository
        extends JpaRepository<DeliveryChallan, Long> {

  //  String findTopByOrderByIdDescChallanNumber();
    DeliveryChallan findTopByOrderByIdDesc();
}