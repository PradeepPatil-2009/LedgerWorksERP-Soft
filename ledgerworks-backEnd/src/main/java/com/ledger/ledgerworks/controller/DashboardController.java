package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.DashboardResponse;
import com.ledger.ledgerworks.service.DashboardService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ✅ Clean endpoint
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }
}