package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByTimestampDesc();

    // Free-text filter (case-insensitive substring) on action / entityType /
    // actor. Pageable carries the newest-first ordering.
    Page<AuditLog>
    findByActionContainingIgnoreCaseOrEntityTypeContainingIgnoreCaseOrActorContainingIgnoreCase(
            String action,
            String entityType,
            String actor,
            Pageable pageable
    );
}
