package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.ProfitLossResponse;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ProfitLossService {

    private final LedgerTransactionRepository transactionRepository;

    public ProfitLossService(LedgerTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ProfitLossResponse generateProfitLoss(
            LocalDate fromDate,
            LocalDate toDate) {

        // =====================================================
        // 1️⃣ FETCH DATA (CORRECT LOGIC)
        // =====================================================

        BigDecimal totalIncome =
                transactionRepository.getIncomeTypeAmount(
                        AccountType.INCOME, fromDate, toDate);

        BigDecimal totalExpense =
                transactionRepository.getExpenseTypeAmount(
                        AccountType.EXPENSE, fromDate, toDate);

        // =====================================================
        // 2️⃣ NULL SAFETY
        // =====================================================

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        // =====================================================
        // 3️⃣ NET PROFIT CALCULATION
        // =====================================================

        BigDecimal netProfit = totalIncome.subtract(totalExpense);

        // =====================================================
        // 4️⃣ RETURN RESPONSE
        // =====================================================

        return new ProfitLossResponse(
                totalIncome,
                totalExpense,
                netProfit
        );
    }
}