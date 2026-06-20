package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.AuditLog;
import com.ledger.ledgerworks.service.AuditLogService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    // GET ALL (newest first)
    @GetMapping
    public List<AuditLog> getAll() {
        return service.getAll();
    }
}
