package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoiceItem;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DOCUMENT -> REPORT end-to-end proof for a SALES INVOICE.
 *
 * <p>Creates a real intra-state {@link Invoice} through {@link InvoiceService}
 * (the genuine production path: GST auto-calc + {@code AccountingPostingService.postSalesInvoice}
 * inside the same {@code @Transactional} save) and then reads the
 * {@link TrialBalanceService} report back to prove the wiring:</p>
 *
 * <ol>
 *   <li>a balanced set of {@link LedgerTransaction}s was created, all stamped
 *       with the INVOICE's own date (not {@code now()});</li>
 *   <li>the Trial Balance for that window has totalDebit == totalCredit;</li>
 *   <li>the Sales (INCOME) credit == totalTaxable;</li>
 *   <li>the Output CGST + Output SGST credit == the invoice's GST;</li>
 *   <li>the customer / debtor debit == grandTotal.</li>
 * </ol>
 *
 * <p><b>Determinism.</b> The shared control accounts (Sales, Output CGST/SGST,
 * Round Off) are global, but the invoice is posted on a UNIQUE far-future date
 * and the report is requested for exactly that one-day window. No seeder posts
 * ledger transactions, and the other ITs use different far-future dates, so only
 * this test's invoice contributes non-zero amounts in this window — the
 * control-account totals are therefore exact. The debtor account is per-customer
 * and given a unique name, so it is wholly owned by this test.</p>
 *
 * <p>Intra-state is forced by configuring the company GSTIN to Maharashtra (27)
 * and tagging the customer with the matching state code (27), so the real
 * {@code GstCalculatorService} emits CGST + SGST (and zero IGST).</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class InvoicePostsToLedgerIT {

    /** Isolated reporting window unique to this IT. */
    private static final LocalDate INVOICE_DATE = LocalDate.of(2090, 5, 12);

    private static final BigDecimal EPSILON = new BigDecimal("0.005");

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private TrialBalanceService trialBalanceService;

    @Autowired
    private LedgerTransactionRepository transactionRepository;

    @Test
    void salesInvoicePostsBalancedJournalAndReconcilesInTrialBalance() {

        // --- Company is in Maharashtra (GST state code 27) so an intra-state
        //     sale produces CGST + SGST through the real GST calculator. ---
        CompanySettings settings = companySettingsService.getSettings();
        settings.setGstNumber("27ABCDE1234F1Z5");
        companySettingsService.saveSettings(settings);

        String customerName = "ACME Test Buyer " + uniq();

        // --- Build a real invoice: 10 units @ 100 = 1000 taxable, 9% + 9% GST. ---
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(INVOICE_DATE);
        invoice.setCustomerName(customerName);
        invoice.setCustomerState("Maharashtra");
        invoice.setCustomerStateCode("27"); // matches company -> intra-state

        InvoiceItem item = new InvoiceItem();
        item.setDescription("Widget");
        item.setHsnCode("8888");
        item.setQuantity(new BigDecimal("10"));
        item.setRate(new BigDecimal("100"));
        item.setCgstRate(new BigDecimal("9"));
        item.setSgstRate(new BigDecimal("9"));
        item.setIgstRate(BigDecimal.ZERO);
        item.setInvoice(invoice);
        invoice.getItems().add(item);

        // --- ACT: real service path (calc + posting inside one transaction). ---
        Invoice saved = invoiceService.create(invoice);

        // The intra-state split must have produced CGST + SGST and no IGST.
        BigDecimal taxable = saved.getTotalTaxable();   // 1000.00
        BigDecimal cgst = saved.getTotalCGST();         // 90.00
        BigDecimal sgst = saved.getTotalSGST();         // 90.00
        BigDecimal igst = saved.getTotalIGST();         // 0.00
        BigDecimal grandTotal = saved.getGrandTotal();  // 1180.00

        assertWithinEpsilon(taxable, new BigDecimal("1000.00"), "invoice taxable");
        assertWithinEpsilon(cgst, new BigDecimal("90.00"), "invoice CGST");
        assertWithinEpsilon(sgst, new BigDecimal("90.00"), "invoice SGST");
        assertWithinEpsilon(igst, BigDecimal.ZERO, "invoice IGST (intra-state must be 0)");
        assertWithinEpsilon(grandTotal, new BigDecimal("1180.00"), "invoice grand total");

        // ============================================================
        // 1) A BALANCED SET OF LEDGER ROWS WAS CREATED ON THE INVOICE DATE.
        // ============================================================
        List<LedgerTransaction> rows = transactionRepository.findAll().stream()
                .filter(t -> INVOICE_DATE.equals(t.getTransactionDate()))
                .toList();

        assertFalse(rows.isEmpty(),
                "Sales invoice must have posted ledger rows on the invoice date");

        // Every row must carry the DOCUMENT's date, not now().
        for (LedgerTransaction t : rows) {
            assertEquals(INVOICE_DATE, t.getTransactionDate(),
                    "Ledger row must be stamped with the invoice date, not now()");
        }

        // (Per-row netting is true by construction for double-entry rows; the
        //  meaningful per-account balance is proven by the Trial Balance below,
        //  which for this isolated window contains only this invoice's legs.)

        // ============================================================
        // 2) TRIAL BALANCE FOR THE WINDOW BALANCES (debit == credit).
        // ============================================================
        TrialBalanceResponse tb =
                trialBalanceService.generateTrialBalance(INVOICE_DATE, INVOICE_DATE);

        assertWithinEpsilon(tb.getTotalDebit(), tb.getTotalCredit(),
                "Trial balance for the invoice window is UNBALANCED");
        assertTrue(tb.isBalanced(),
                "TrialBalanceService must flag the invoice window balanced");

        // Total debit for this isolated window == grandTotal (debtor) + grandTotal
        // worth of credit legs == grandTotal on each side.
        assertWithinEpsilon(tb.getTotalDebit(), grandTotal,
                "Total debit for the window must equal the invoice grand total");

        // ============================================================
        // 3) SALES (INCOME) CREDIT == totalTaxable.
        // ============================================================
        TrialBalanceRow salesRow = row(tb, AccountingPostingService.SALES);
        assertWithinEpsilon(nz(salesRow.getCredit()), taxable,
                "Sales credit must equal the invoice taxable value");
        assertWithinEpsilon(nz(salesRow.getDebit()), BigDecimal.ZERO,
                "Sales must not be debited by a plain sale");

        // ============================================================
        // 4) OUTPUT CGST + OUTPUT SGST CREDIT == invoice GST.
        // ============================================================
        TrialBalanceRow cgstRow = row(tb, AccountingPostingService.OUTPUT_CGST);
        TrialBalanceRow sgstRow = row(tb, AccountingPostingService.OUTPUT_SGST);

        assertWithinEpsilon(nz(cgstRow.getCredit()), cgst, "Output CGST credit");
        assertWithinEpsilon(nz(sgstRow.getCredit()), sgst, "Output SGST credit");

        BigDecimal outputGst = nz(cgstRow.getCredit()).add(nz(sgstRow.getCredit()));
        assertWithinEpsilon(outputGst, cgst.add(sgst),
                "Output CGST + SGST credit must equal the invoice's total GST");

        // ============================================================
        // 5) CUSTOMER / DEBTOR DEBIT == grandTotal.
        // ============================================================
        TrialBalanceRow debtorRow = row(tb, customerName);
        assertWithinEpsilon(nz(debtorRow.getDebit()), grandTotal,
                "Customer (debtor) debit must equal the invoice grand total");
        assertWithinEpsilon(nz(debtorRow.getCredit()), BigDecimal.ZERO,
                "Customer must not be credited by the original sale");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static TrialBalanceRow row(TrialBalanceResponse tb, String accountName) {
        return tb.getRows().stream()
                .filter(r -> accountName.equals(r.getAccountName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Expected account '" + accountName
                                + "' in the trial balance for the invoice window"));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static void assertWithinEpsilon(BigDecimal actual, BigDecimal expected,
                                            String message) {
        assertTrue(actual != null, message + " must not be null");
        BigDecimal diff = actual.subtract(expected).abs();
        assertTrue(diff.compareTo(EPSILON) < 0,
                message + " (actual=" + actual + ", expected=" + expected
                        + ", diff=" + diff + ")");
    }

    private static long uniq() {
        return System.nanoTime();
    }
}
