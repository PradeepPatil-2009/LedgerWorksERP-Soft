package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the Trial Balance report is internally consistent: every balanced
 * accounting transaction (an equal-and-opposite debit/credit pair) must leave
 * the report's total debits equal to its total credits.
 *
 * <p>The {@link TrialBalanceService} drives off {@code LedgerAccount} LEFT JOIN
 * {@code LedgerTransaction} with the date filter applied to the join, so accounts
 * created by other tests appear only as zero-zero rows and never unbalance the
 * totals. The debit==credit invariant therefore holds for any window. For the
 * penny-exact total assertions, the seeded transactions are posted on a unique
 * far-future date and the report is requested for exactly that window, so only
 * this test's transactions contribute non-zero amounts — deterministic on the
 * shared in-memory schema regardless of what other tests have written.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class TrialBalanceBalancesIT {

    /** Isolated reporting window: nothing else in the schema posts here. */
    private static final LocalDate POSTING_DATE = LocalDate.of(2099, 1, 15);

    /** Equality tolerance for monetary comparisons. */
    private static final BigDecimal EPSILON = new BigDecimal("0.005");

    @Autowired
    private TrialBalanceService trialBalanceService;

    @Autowired
    private LedgerTransactionService ledgerTransactionService;

    @Autowired
    private LedgerAccountRepository accountRepository;

    @Autowired
    private LedgerTransactionRepository transactionRepository;

    @Test
    void postedBalancedTransactionThroughServiceKeepsDebitsEqualToCredits() {
        // --- Seed two accounts to move money between. ---
        LedgerAccount cash = saveAccount("TB-Cash-" + uniq(), AccountType.ASSET);
        LedgerAccount sales = saveAccount("TB-Sales-" + uniq(), AccountType.INCOME);

        // --- Post a BALANCED transaction through the REAL service: a single
        //     debit/credit pair of the SAME amount. The service stamps the
        //     transaction with today's date, so we query the report for today. ---
        BigDecimal amount = new BigDecimal("1500.00");
        ledgerTransactionService.createTransaction(
                cash.getId(), sales.getId(), amount, "Sale settled into cash");

        LocalDate today = LocalDate.now();

        // --- Generate the report for today's window. ---
        TrialBalanceResponse report =
                trialBalanceService.generateTrialBalance(today, today);

        assertNotNull(report, "Trial balance report must be produced");
        assertNotNull(report.getRows(), "Trial balance rows must not be null");

        // --- INVARIANT 1: SUM(debits) == SUM(credits). ---
        BigDecimal totalDebit = report.getTotalDebit();
        BigDecimal totalCredit = report.getTotalCredit();
        assertWithinEpsilon(totalDebit, totalCredit,
                "Trial balance is UNBALANCED: total debit " + totalDebit
                        + " != total credit " + totalCredit
                        + " (a true double-entry posting bug)");

        // The service's own balanced flag must agree.
        assertTrue(report.isBalanced(),
                "TrialBalanceService reported balanced=false while debit==credit");

        // NOTE: we do NOT assert the grand total equals our single amount here,
        // because other tests may also post transactions dated today into the
        // shared in-memory schema. The penny-exact total checks live in the
        // isolated far-future-window tests below. What MUST always hold for
        // today's window is debit==credit, asserted above.

        // --- INVARIANT 2: both affected accounts appear with the right sides. ---
        TrialBalanceRow cashRow = findRow(report.getRows(), cash.getAccountName());
        TrialBalanceRow salesRow = findRow(report.getRows(), sales.getAccountName());

        assertWithinEpsilon(nz(cashRow.getDebit()), amount,
                "Debited (cash) account must show the amount on the debit side");
        assertWithinEpsilon(nz(cashRow.getCredit()), BigDecimal.ZERO,
                "Debited (cash) account must show zero on the credit side");

        assertWithinEpsilon(nz(salesRow.getCredit()), amount,
                "Credited (sales) account must show the amount on the credit side");
        assertWithinEpsilon(nz(salesRow.getDebit()), BigDecimal.ZERO,
                "Credited (sales) account must show zero on the debit side");
    }

    @Test
    void multipleBalancedPostingsRemainBalancedAndTotalToTheirSum() {
        // Seed four accounts and post several equal-and-opposite pairs, mixing
        // amounts and direction, all on the ISOLATED far-future date so the
        // grand total can be asserted to the penny (nothing else posts there).
        LedgerAccount bank = saveAccount("TB-Bank-" + uniq(), AccountType.ASSET);
        LedgerAccount customer = saveAccount("TB-Customer-" + uniq(), AccountType.ASSET);
        LedgerAccount revenue = saveAccount("TB-Revenue-" + uniq(), AccountType.INCOME);
        LedgerAccount expense = saveAccount("TB-Expense-" + uniq(), AccountType.EXPENSE);

        // Invoice: debit customer, credit revenue.
        persistTransaction(customer, revenue, new BigDecimal("999.99"), POSTING_DATE);
        // Receipt: debit bank, credit customer.
        persistTransaction(bank, customer, new BigDecimal("999.99"), POSTING_DATE);
        // Expense: debit expense, credit bank.
        persistTransaction(expense, bank, new BigDecimal("123.45"), POSTING_DATE);

        TrialBalanceResponse report =
                trialBalanceService.generateTrialBalance(POSTING_DATE, POSTING_DATE);

        BigDecimal expectedTotal = new BigDecimal("2123.43"); // 999.99 + 999.99 + 123.45

        assertWithinEpsilon(report.getTotalDebit(), report.getTotalCredit(),
                "Aggregate trial balance must stay balanced across many postings");
        assertWithinEpsilon(report.getTotalDebit(), expectedTotal,
                "Total debit must equal the sum of all posted amounts");
        assertWithinEpsilon(report.getTotalCredit(), expectedTotal,
                "Total credit must equal the sum of all posted amounts");
        assertTrue(report.isBalanced(),
                "Aggregate report must be flagged balanced");
    }

    @Test
    void directlyPersistedEqualOppositeTransactionBalances() {
        // Same invariant, proven without the posting service: persist a
        // LedgerTransaction of equal opposite amounts straight into the repo on
        // its own isolated window.
        LedgerAccount debitAcc = saveAccount("TB-Direct-Dr-" + uniq(), AccountType.EXPENSE);
        LedgerAccount creditAcc = saveAccount("TB-Direct-Cr-" + uniq(), AccountType.LIABILITY);

        BigDecimal amount = new BigDecimal("742.10");
        LocalDate window = LocalDate.of(2098, 6, 30);

        persistTransaction(debitAcc, creditAcc, amount, window);

        TrialBalanceResponse report =
                trialBalanceService.generateTrialBalance(window, window);

        assertWithinEpsilon(report.getTotalDebit(), report.getTotalCredit(),
                "Directly persisted equal/opposite entry must balance");
        assertWithinEpsilon(report.getTotalDebit(), amount,
                "Total debit must equal the directly persisted amount");
        assertTrue(report.isBalanced(), "Direct-persist report must be balanced");
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

    private void persistTransaction(LedgerAccount debit, LedgerAccount credit,
                                    BigDecimal amount, LocalDate date) {
        // One LedgerTransaction row already encodes a balanced double entry:
        // the same amount is a debit to one account and a credit to the other.
        // We persist directly so we can pin the transactionDate to an isolated
        // window (the service forces today's date) and set a unique, collision-
        // free transactionNumber.
        LedgerTransaction t = new LedgerTransaction();
        t.setTransactionNumber("TB-" + uniq());
        t.setDebitAccount(debit);
        t.setCreditAccount(credit);
        t.setAmount(amount);
        t.setTransactionDate(date);
        t.setNarration("Seeded balanced entry");
        transactionRepository.saveAndFlush(t);
    }

    private static TrialBalanceRow findRow(List<TrialBalanceRow> rows, String accountName) {
        return rows.stream()
                .filter(r -> accountName.equals(r.getAccountName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Expected account '" + accountName
                                + "' to appear in the trial balance rows"));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static void assertWithinEpsilon(BigDecimal actual, BigDecimal expected,
                                            String message) {
        BigDecimal diff = actual.subtract(expected).abs();
        assertTrue(diff.compareTo(EPSILON) < 0,
                message + " (actual=" + actual + ", expected=" + expected
                        + ", diff=" + diff + ")");
    }

    private static long uniq() {
        return System.nanoTime();
    }
}
