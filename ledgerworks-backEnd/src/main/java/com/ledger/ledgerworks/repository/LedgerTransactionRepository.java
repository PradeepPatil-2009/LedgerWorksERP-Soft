package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.dto.MonthlySummary;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.enums.AccountType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, Long> {

    // =====================================================
    // 1️⃣ TRIAL BALANCE
    // =====================================================

    @Query("""
        SELECT new com.ledger.ledgerworks.dto.TrialBalanceRow(
            a.accountName,
            COALESCE(SUM(CASE WHEN t.debitAccount.id = a.id THEN t.amount ELSE 0 END),0),
            COALESCE(SUM(CASE WHEN t.creditAccount.id = a.id THEN t.amount ELSE 0 END),0)
        )
        FROM LedgerAccount a
        LEFT JOIN LedgerTransaction t
            ON (t.debitAccount.id = a.id OR t.creditAccount.id = a.id)
        GROUP BY a.id, a.accountName
        ORDER BY a.accountName
    """)
    List<TrialBalanceRow> getTrialBalance();


    // =====================================================
    // 2️⃣ TRIAL BALANCE WITH DATE FILTER
    // =====================================================

    @Query("""
        SELECT new com.ledger.ledgerworks.dto.TrialBalanceRow(
            a.accountName,
            COALESCE(SUM(CASE WHEN t.debitAccount.id = a.id THEN t.amount ELSE 0 END),0),
            COALESCE(SUM(CASE WHEN t.creditAccount.id = a.id THEN t.amount ELSE 0 END),0)
        )
        FROM LedgerAccount a
        LEFT JOIN LedgerTransaction t
            ON (t.debitAccount.id = a.id OR t.creditAccount.id = a.id)
            AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
            AND (:toDate IS NULL OR t.transactionDate <= :toDate)
        GROUP BY a.id, a.accountName
        ORDER BY a.accountName
    """)
    List<TrialBalanceRow> getTrialBalanceWithDate(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // =====================================================
    // 3️⃣ PROFIT & LOSS  (per-direction aggregation)
    //
    // A multi-leg journal is many rows sharing one side, so each posting
    // direction must be summed independently and then netted:
    //
    //   INCOME  = credit-side income  - debit-side income
    //   EXPENSE = debit-side expense  - credit-side expense
    //
    // Subtracting the opposite direction makes reversals / returns (which
    // post on the contra side of the same account type) reduce the figure
    // instead of inflating it.
    // =====================================================

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.creditAccount.accountType = :type THEN t.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN t.debitAccount.accountType  = :type THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getIncomeTypeAmount(
            @Param("type") AccountType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.debitAccount.accountType  = :type THEN t.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN t.creditAccount.accountType = :type THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getExpenseTypeAmount(
            @Param("type") AccountType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // =====================================================
    // 4️⃣ BALANCE SHEET  (per-direction aggregation)
    //
    // Debit-natured types (ASSET, EXPENSE) carry a debit balance:
    //   balance = debit-side sum - credit-side sum
    // Credit-natured types (LIABILITY, CAPITAL, INCOME) carry a credit
    // balance: balance = credit-side sum - debit-side sum.
    //
    // Both directions are summed independently per account type, then netted.
    // =====================================================

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.debitAccount.accountType  = :type THEN t.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN t.creditAccount.accountType = :type THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getDebitBalanceByAccountType(
            @Param("type") AccountType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.creditAccount.accountType = :type THEN t.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN t.debitAccount.accountType  = :type THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getCreditBalanceByAccountType(
            @Param("type") AccountType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // =====================================================
    // 4️⃣b CASH FLOW  (movement on named cash/bank accounts)
    //
    // Net movement (debits - credits) over the period on a set of ledger
    // accounts identified by name (e.g. "Cash", "Bank"). A positive result
    // is a net cash inflow for the window.
    // =====================================================

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.debitAccount.accountName  IN :accountNames THEN t.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN t.creditAccount.accountName IN :accountNames THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getNetMovementForAccounts(
            @Param("accountNames") List<String> accountNames,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.debitAccount.accountName IN :accountNames THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getInflowForAccounts(
            @Param("accountNames") List<String> accountNames,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN t.creditAccount.accountName IN :accountNames THEN t.amount ELSE 0 END), 0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getOutflowForAccounts(
            @Param("accountNames") List<String> accountNames,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // =====================================================
    // 5️⃣ LEDGER BY ACCOUNT NAME
    // =====================================================

    @Query("""
        SELECT t
        FROM LedgerTransaction t
        WHERE (t.debitAccount.accountName = :account
               OR t.creditAccount.accountName = :account)
        AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
        AND (:toDate IS NULL OR t.transactionDate <= :toDate)
        ORDER BY t.transactionDate
    """)
    List<LedgerTransaction> getLedgerEntries(
            @Param("account") String account,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // =====================================================
    // 6️⃣ LEDGER BY ACCOUNT ID
    // =====================================================

    @Query("""
        SELECT t
        FROM LedgerTransaction t
        WHERE (t.debitAccount.id = :accountId
               OR t.creditAccount.id = :accountId)
        AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
        AND (:toDate IS NULL OR t.transactionDate <= :toDate)
        ORDER BY t.transactionDate
    """)
    List<LedgerTransaction> findByAccountAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // =====================================================
    // 7️⃣ MONTHLY REPORT
    // =====================================================

    @Query("""
        SELECT new com.ledger.ledgerworks.dto.MonthlySummary(
            CAST(FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') AS string),
            SUM(t.amount)
        )
        FROM LedgerTransaction t
        GROUP BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
    """)
    List<MonthlySummary> getMonthlyReceipts();
}