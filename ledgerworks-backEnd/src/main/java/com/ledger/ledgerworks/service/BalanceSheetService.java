package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.BalanceSheetResponse;
import com.ledger.ledgerworks.dto.ProfitLossResponse;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class BalanceSheetService {

    private final LedgerTransactionRepository transactionRepository;
    private final ProfitLossService profitLossService;

    public BalanceSheetService(
            LedgerTransactionRepository transactionRepository,
            ProfitLossService profitLossService) {

        this.transactionRepository = transactionRepository;
        this.profitLossService = profitLossService;
    }

    public BalanceSheetResponse generateBalanceSheet(
            LocalDate fromDate,
            LocalDate toDate) {

        // =====================================================
        // 1️⃣ FETCH VALUES (CORRECT ACCOUNTING LOGIC)
        // =====================================================

        BigDecimal totalAssets =
                transactionRepository.getBalanceByAccountType(
                        AccountType.ASSET, fromDate, toDate);

        BigDecimal totalLiabilities =
                transactionRepository.getBalanceByAccountType(
                        AccountType.LIABILITY, fromDate, toDate);

        BigDecimal totalCapital =
                transactionRepository.getBalanceByAccountType(
                        AccountType.CAPITAL, fromDate, toDate);

        // =====================================================
        // 2️⃣ NULL SAFETY
        // =====================================================

        totalAssets = totalAssets != null ? totalAssets : BigDecimal.ZERO;
        totalLiabilities = totalLiabilities != null ? totalLiabilities : BigDecimal.ZERO;
        totalCapital = totalCapital != null ? totalCapital : BigDecimal.ZERO;

        // =====================================================
        // 3️⃣ GET PROFIT & LOSS
        // =====================================================

        ProfitLossResponse profitLoss =
                profitLossService.generateProfitLoss(fromDate, toDate);

        BigDecimal netProfit =
                profitLoss.getNetProfit() != null
                        ? profitLoss.getNetProfit()
                        : BigDecimal.ZERO;

        // =====================================================
        // 4️⃣ BALANCE SHEET FORMULA
        // Assets = Liabilities + Capital + Profit
        // =====================================================

        BigDecimal rightSideTotal =
                totalLiabilities
                        .add(totalCapital)
                        .add(netProfit);

        boolean balanced =
                totalAssets.compareTo(rightSideTotal) == 0;

        // =====================================================
        // 5️⃣ RETURN RESPONSE (ROUNDED)
        // =====================================================

        return new BalanceSheetResponse(
                totalAssets.setScale(2, RoundingMode.HALF_UP),
                totalLiabilities.setScale(2, RoundingMode.HALF_UP),
                netProfit.setScale(2, RoundingMode.HALF_UP),
                balanced
        );
    }
}