package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.NumberSeries;
import com.ledger.ledgerworks.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumberSeriesRepository extends JpaRepository<NumberSeries, Long> {

    Optional<NumberSeries> findByDocumentType(DocumentType documentType);
}
