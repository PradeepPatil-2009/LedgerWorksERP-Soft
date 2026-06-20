package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.AuditLog;
import com.ledger.ledgerworks.repository.AuditLogRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    // Record an audit event. Best-effort: never throws back to the caller.
    public void record(String action, String entityType, String detail) {

        try {

            AuditLog log = new AuditLog();

            log.setAction(action);
            log.setEntityType(entityType);
            log.setDetail(truncate(detail));
            log.setUsername(currentUsername());
            log.setTimestamp(LocalDateTime.now());

            repository.save(log);

        } catch (Exception ignored) {
            // Auditing must never break the underlying request.
        }
    }

    public List<AuditLog> getAll() {
        return repository.findAllByOrderByTimestampDesc();
    }

    private String currentUsername() {

        try {

            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }

        } catch (Exception ignored) {
        }

        return "system";
    }

    private String truncate(String value) {

        if (value == null) {
            return null;
        }

        return value.length() > 2000
                ? value.substring(0, 2000)
                : value;
    }
}
