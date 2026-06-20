package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.AuditLog;
import com.ledger.ledgerworks.service.AuditLogService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    // GET (server-paginated, newest first). Optional free-text q filters
    // action / entityType / actor (case-insensitive). ADMIN-only via
    // SecurityConfig. Returns a Spring Data Page<AuditLog>.
    @GetMapping
    public Page<AuditLog> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q
    ) {
        return service.getPage(page, size, q);
    }
}
