package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.MaterialIssueItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialIssueItemRepository
        extends JpaRepository<MaterialIssueItem, Long> {
}