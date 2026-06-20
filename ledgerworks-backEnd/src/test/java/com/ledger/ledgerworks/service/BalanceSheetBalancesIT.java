package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.BalanceSheetResponse;
import com.ledger.ledgerworks.dto.ProfitLossResponse;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the Balance Sheet report's balancing invariant:
 * <pre>
 *     Assets = Liabilities + Capital + Net&nbsp;Profit
 * </pre>
 * which is exactly what {@link BalanceSheetService} computes. The balance-sheet
 * and profit-&amp;-loss queries filter {@code LedgerTransaction} by date directly
 * (not via the chart of accounts), so seeding on a unique far-future date and
 * requesting that window fully isolates this test's data from anything else in
 * the shared in-memory schema — making the totals deterministic.
 *
 * <p>The scenario deliberately uses no CAPITAL account, so the invariant reduces
 * to {@code Assets == Liabilities + NetProfit}; all three terms are exposed on
 * {@link BalanceSheetResponse}, letting the test reconcile them to the penny in
 * addition to trusting the service's own {@code balanced} flag.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class BalanceSheetBalancesIT {

    /** Isolated reporting window: nothing else in the schema posts here. */
    private static final LocalDate BS_DATE = LocalDate.of(2097, 3, 10);

    private static final BigDecimal EPSILON = new BigDecimal("0.005");

    @Autowired
    private BalanceSheetService balanceSheetService;

    @Autowired
    private ProfitLossService profitLossService;

    @Autowired
    private LedgerAccountRepository accountRepository;

    @Autowired
    private LedgerTransactionRepository transactionRepository;

    @Test
    void balanceSheetSatisfiesAssetsEqualsLiabilitiesPlusProfit() {
        // --- Chart of accounts for a self-contained balanced scenario. ---
        LedgerAccount bank = saveAccount("BS-Bank-" + uniq(), AccountType.ASSET);
        LedgerAccount loan = saveAccount("BS-Loan-" + uniq(), AccountType.LIABILITY);
        LedgerAccount sales = saveAccount("BS-Sales-" + uniq(), AccountType.INCOME);
        LedgerAccount rent = saveAccount("BS-Rent-" + uniq(), AccountType.EXPENSE);

        // 1) Earn revenue into the bank: Dr Bank (ASSET) / Cr Sales (INCOME) 1000.
        //    Assets +1000, Income +1000.
        persist(bank, sales, new BigDecimal("1000.00"));

        // 2) Pay rent out of the bank: Dr Rent (EXPENSE) / Cr Bank (ASSET) 300.
        //    Assets -300, Expense +300.
        persist(rent, bank, new BigDecimal("300.00"));

        // 3) Take a loan into the bank: Dr Bank (ASSET) / Cr Loan (LIABILITY) 500.
        //    Assets +500, Liabilities +500.
        persist(bank, loan, new BigDecimal("500.00"));

        // Expected:
        //   Assets       = 1000 - 300 + 500 = 1200
        //   Liabilities  = 500
        //   Net profit   = income(1000) - expense(300) = 700
        //   Liabilities + profit = 500 + 700 = 1200 == Assets  -> BALANCED
        BigDecimal expectedAssets = new BigDecimal("1200.00");
        BigDecimal expectedLiabilities = new BigDecimal("500.00");
        BigDecimal expectedNetProfit = new BigDecimal("700.00");

        // --- Profit & Loss must reconcile on its own first. ---
        ProfitLossResponse pl = profitLossService.generateProfitLoss(BS_DATE, BS_DATE);
        assertNotNull(pl, "P&L report must be produced");
        assertWithinEpsilon(pl.getTotalIncome(), new BigDecimal("1000.00"),
                "P&L total income");
        assertWithinEpsilon(pl.getTotalExpense(), new BigDecimal("300.00"),
                "P&L total expense");
        assertWithinEpsilon(pl.getNetProfit(), expectedNetProfit,
                "P&L net profit = income - expense");

        // --- Balance Sheet. ---
        BalanceSheetResponse bs = balanceSheetService.generateBalanceSheet(BS_DATE, BS_DATE);
        assertNotNull(bs, "Balance sheet report must be produced");

        assertWithinEpsilon(bs.getTotalAssets(), expectedAssets,
                "Balance sheet total assets");
        assertWithinEpsilon(bs.getTotalLiabilities(), expectedLiabilities,
                "Balance sheet total liabilities");
        assertWithinEpsilon(bs.getNetProfit(), expectedNetProfit,
                "Balance sheet net profit (carried from P&L)");

        // --- THE INVARIANT: Assets == Liabilities + NetProfit (no capital here). ---
        BigDecimal rightSide = bs.getTotalLiabilities().add(bs.getNetProfit());
        assertWithinEpsilon(bs.getTotalAssets(), rightSide,
                "Balance sheet is UNBALANCED: assets " + bs.getTotalAssets()
                        + " != liabilities + profit " + rightSide
                        + " (a true accounting-identity bug)");

        // --- The service's own balanced flag must agree. ---
        assertTrue(bs.isBalanced(),
                "BalanceSheetService reported balanced=false while the accounting "
                        + "identity holds (assets == liabilities + capital + profit)");
    }

    @Test
    void emptyWindowProducesAZeroedButBalancedReport() {
        // A window with no transactions must still compute without error and
        // reconcile trivially: everything zero, balanced == true.
        LocalDate emptyWindow = LocalDate.of(2096, 12, 1);

        BalanceSheetResponse bs =
                balanceSheetService.generateBalanceSheet(emptyWindow, emptyWindow);

        assertNotNull(bs, "Balance sheet must be produced for an empty window");
        assertWithinEpsilon(bs.getTotalAssets(), BigDecimal.ZERO, "Assets must be zero");
        assertWithinEpsilon(bs.getTotalLiabilities(), BigDecimal.ZERO,
                "Liabilities must be zero");
        assertWithinEpsilon(bs.getNetProfit(), BigDecimal.ZERO, "Net profit must be zero");
        assertTrue(bs.isBalanced(),
                "An all-zero balance sheet must be flagged balanced");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private LedgerAccount saveAccount(String name, AccountType type) {
        LedgerAccount a = new LedgerAccount();
        a.setAccountName(name);
        a.setAccountType(type);
        a.setBalance(BigDecimal.ZERO);
        return accountRepository.saveAndFlush(a);
    }

    private void persist(LedgerAccount debit, LedgerAccount credit, BigDecimal amount) {
        LedgerTransaction t = new LedgerTransaction();
        t.setTransactionNumber("BS-" + uniq());
        t.setDebitAccount(debit);
        t.setCreditAccount(credit);
        t.setAmount(amount);
        t.setTransactionDate(BS_DATE);
        t.setNarration("Seeded balance-sheet entry");
        transactionRepository.saveAndFlush(t);
    }

    private static void assertWithinEpsilon(BigDecimal actual, BigDecimal expected,
                                            String message) {
        assertNotNull(actual, message + " must not be null");
        BigDecimal diff = actual.subtract(expected).abs();
        assertTrue(diff.compareTo(EPSILON) < 0,
                message + " (actual=" + actual + ", expected=" + expected
                        + ", diff=" + diff + ")");
    }

    private static long uniq() {
        return System.nanoTime();
    }
}
