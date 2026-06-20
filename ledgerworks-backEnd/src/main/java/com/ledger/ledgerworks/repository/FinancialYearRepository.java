package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {

    Optional<FinancialYear> findByActiveTrue();

    Optional<FinancialYear> findByName(String name);

    List<FinancialYear> findAllByOrderByStartDateDesc();
}
