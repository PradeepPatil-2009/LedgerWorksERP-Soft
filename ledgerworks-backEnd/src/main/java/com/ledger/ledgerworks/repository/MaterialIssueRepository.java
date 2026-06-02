package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.MaterialIssue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialIssueRepository
        extends JpaRepository<MaterialIssue, Long> {
}