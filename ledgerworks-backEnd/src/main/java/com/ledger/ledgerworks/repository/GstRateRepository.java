package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.GstRate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GstRateRepository extends JpaRepository<GstRate, Long> {

    List<GstRate> findByActiveTrueOrderByRateAsc();
}
