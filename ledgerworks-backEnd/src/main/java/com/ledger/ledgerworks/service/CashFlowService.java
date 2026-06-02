package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.CashFlowResponse;
import com.ledger.ledgerworks.dto.CashFlowRow;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CashFlowService {

    private final LedgerTransactionRepository repository;

    public CashFlowService(LedgerTransactionRepository repository) {
        this.repository = repository;
    }

    public CashFlowResponse generateCashFlow(
            LocalDate fromDate,
            LocalDate toDate) {

        // =====================================================
        // 1️⃣ FETCH DATA (CORRECT METHODS)
        // =====================================================

        BigDecimal operatingInflow =
                repository.getProfitLossAmount(
                        AccountType.INCOME, fromDate, toDate);

        BigDecimal operatingOutflow =
                repository.getProfitLossAmount(
                        AccountType.EXPENSE, fromDate, toDate);

        BigDecimal investingOutflow =
                repository.getBalanceByAccountType(
                        AccountType.ASSET, fromDate, toDate);

        BigDecimal financingInflow =
                repository.getBalanceByAccountType(
                        AccountType.CAPITAL, fromDate, toDate);

        // =====================================================
        // 2️⃣ NULL SAFETY
        // =====================================================

        operatingInflow = safe(operatingInflow);
        operatingOutflow = safe(operatingOutflow);
        investingOutflow = safe(investingOutflow);
        financingInflow = safe(financingInflow);

        // =====================================================
        // 3️⃣ BUILD ROWS
        // =====================================================

        List<CashFlowRow> rows = new ArrayList<>();

        rows.add(new CashFlowRow(
                "Operating Activities",
                operatingInflow,
                operatingOutflow
        ));

        rows.add(new CashFlowRow(
                "Investing Activities",
                BigDecimal.ZERO,
                investingOutflow
        ));

        rows.add(new CashFlowRow(
                "Financing Activities",
                financingInflow,
                BigDecimal.ZERO
        ));

        // =====================================================
        // 4️⃣ TOTAL CALCULATION
        // =====================================================

        BigDecimal totalInflow =
                operatingInflow.add(financingInflow);

        BigDecimal totalOutflow =
                operatingOutflow.add(investingOutflow);

        BigDecimal netCash =
                totalInflow.subtract(totalOutflow);

        // =====================================================
        // 5️⃣ RETURN RESPONSE
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