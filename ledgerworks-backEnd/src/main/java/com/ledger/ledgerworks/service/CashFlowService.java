package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.CashFlowResponse;
import com.ledger.ledgerworks.dto.CashFlowRow;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CashFlowService {

    /**
     * Ledger accounts that represent liquid cash. Movement on these accounts
     * (debits = cash coming in, credits = cash going out) is the cash flow.
     * These are the standard names seeded by the chart-of-accounts initializer.
     */
    private static final List<String> CASH_ACCOUNTS = List.of("Cash", "Bank");

    private final LedgerTransactionRepository repository;

    public CashFlowService(LedgerTransactionRepository repository) {
        this.repository = repository;
    }

    public CashFlowResponse generateCashFlow(
            LocalDate fromDate,
            LocalDate toDate) {

        // =====================================================
        // 1️⃣ FETCH CASH MOVEMENTS
        // Inflow  = debits posted to Cash/Bank in the period.
        // Outflow = credits posted to Cash/Bank in the period.
        // =====================================================

        BigDecimal totalInflow =
                safe(repository.getInflowForAccounts(
                        CASH_ACCOUNTS, fromDate, toDate));

        BigDecimal totalOutflow =
                safe(repository.getOutflowForAccounts(
                        CASH_ACCOUNTS, fromDate, toDate));

        BigDecimal netCash = totalInflow.subtract(totalOutflow);

        // =====================================================
        // 2️⃣ BUILD ROWS
        // The model carries no per-activity classification, so the net
        // movement is reported as a single cash line.
        // =====================================================

        List<CashFlowRow> rows = new ArrayList<>();

        rows.add(new CashFlowRow(
                "Cash & Bank Movement",
                totalInflow,
                totalOutflow
        ));

        // =====================================================
        // 3️⃣ RETURN RESPONSE
        // =====================================================

        return new CashFlowResponse(
                rows,
                totalInflow,
                totalOutflow,
                netCash
        );
    }

    // =====================================================
    // HELPER METHOD
    // =====================================================

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
