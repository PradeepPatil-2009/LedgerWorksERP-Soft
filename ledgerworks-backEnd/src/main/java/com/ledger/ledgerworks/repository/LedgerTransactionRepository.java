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
    // 3️⃣ PROFIT & LOSS (🔥 CORRECT LOGIC)
    // =====================================================

    @Query("""
    	    SELECT COALESCE(SUM(
    	        CASE 
    	            WHEN :type = com.ledger.ledgerworks.enums.AccountType.INCOME
    	                 AND t.creditAccount.accountType = :type THEN t.amount

    	            WHEN :type = com.ledger.ledgerworks.enums.AccountType.EXPENSE
    	                 AND t.debitAccount.accountType = :type THEN t.amount

    	            ELSE 0
    	        END
    	    ),0)
    	    FROM LedgerTransaction t
    	    WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
    	    AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    	""")
    	BigDecimal getProfitLossAmount(
    	        @Param("type") AccountType type,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate
    	);

    // =====================================================
    // 4️⃣ BALANCE SHEET (🔥 CORRECT ACCOUNTING LOGIC)
    // =====================================================

    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN :type IN ('ASSET','EXPENSE') THEN
                    CASE 
                        WHEN t.debitAccount.accountType = :type THEN t.amount
                        WHEN t.creditAccount.accountType = :type THEN -t.amount
                        ELSE 0
                    END
                ELSE
                    CASE 
                        WHEN t.creditAccount.accountType = :type THEN t.amount
                        WHEN t.debitAccount.accountType = :type THEN -t.amount
                        ELSE 0
                    END
            END
        ),0)
        FROM LedgerTransaction t
        WHERE (:fromDate IS NULL OR t.transactionDate >= :fromDate)
        AND (:toDate IS NULL OR t.transactionDate <= :toDate)
    """)
    BigDecimal getBalanceByAccountType(
            @Param("type") AccountType type,
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
            FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m'),
            SUM(t.amount)
        )
        FROM LedgerTransaction t
        GROUP BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
    """)
    List<MonthlySummary> getMonthlyReceipts();
}