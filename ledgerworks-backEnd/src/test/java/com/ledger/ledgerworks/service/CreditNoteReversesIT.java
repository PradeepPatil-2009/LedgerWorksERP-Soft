package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.CreditNote;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoiceItem;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DOCUMENT -> REPORT end-to-end proof that a CREDIT NOTE (sales return) reverses
 * the original sale in the ledger and reduces the referenced invoice's
 * receivable.
 *
 * <p>Flow: create a real intra-state {@link Invoice} via {@link InvoiceService},
 * then a {@link CreditNote} referencing it via {@link CreditNoteService} (which
 * routes through {@code AccountingPostingService.postSalesReturn} +
 * {@code applyToReferencedInvoice}). Both are posted on the SAME isolated
 * far-future window and to the SAME unique customer, so the invoice's legs and
 * the credit note's reversing legs are the only postings in the window.</p>
 *
 * <p>For a FULL return the reversal exactly cancels the sale, so within the
 * window:</p>
 * <ul>
 *   <li>Sales net (credit - debit) == 0 — the income was reversed;</li>
 *   <li>Output CGST net == 0 and Output SGST net == 0 — output tax reversed;</li>
 *   <li>the debtor net (debit - credit) == 0 — the receivable was cancelled;</li>
 *   <li>the referenced invoice's outstanding is reduced to 0 and marked PAID.</li>
 * </ul>
 *
 * <p>The net-zero assertions are stronger than "some reversal happened": they
 * prove the reversal is equal-and-opposite to the sale on every account.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class CreditNoteReversesIT {

    private static final LocalDate DOC_DATE = LocalDate.of(2092, 8, 14);

    private static final BigDecimal EPSILON = new BigDecimal("0.005");

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CreditNoteService creditNoteService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private TrialBalanceService trialBalanceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void creditNoteReversesSaleAndReducesInvoiceOutstanding() {

        CompanySettings settings = companySettingsService.getSettings();
        settings.setGstNumber("27ABCDE1234F1Z5");
        companySettingsService.saveSettings(settings);

        String customerName = "Return Buyer " + uniq();

        // ----- 1) Original intra-state invoice: 1000 taxable, 90 + 90 GST. -----
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(DOC_DATE);
        invoice.setCustomerName(customerName);
        invoice.setCustomerStateCode("27");

        InvoiceItem item = new InvoiceItem();
        item.setDescription("Returnable Widget");
        item.setQuantity(new BigDecimal("10"));
        item.setRate(new BigDecimal("100"));
        item.setCgstRate(new BigDecimal("9"));
        item.setSgstRate(new BigDecimal("9"));
        item.setInvoice(invoice);
        invoice.getItems().add(item);

        Invoice savedInvoice = invoiceService.create(invoice);

        BigDecimal taxable = savedInvoice.getTotalTaxable();   // 1000.00
        BigDecimal cgst = savedInvoice.getTotalCGST();         // 90.00
        BigDecimal sgst = savedInvoice.getTotalSGST();         // 90.00
        BigDecimal grandTotal = savedInvoice.getGrandTotal();  // 1180.00
        String invoiceNumber = savedInvoice.getInvoiceNumber();

        // Sanity: the invoice opened with the full grand total outstanding.
        assertWithinEpsilon(savedInvoice.getOutstandingAmount(), grandTotal,
                "Invoice should open fully outstanding");

        // ----- 2) Full credit note referencing the invoice. -----
        CreditNote note = new CreditNote();
        note.setDate(DOC_DATE);
        note.setPartyName(customerName);
        note.setInvoiceReference(invoiceNumber);
        note.setTaxableValue(taxable);
        note.setCgstAmount(cgst);
        note.setSgstAmount(sgst);
        note.setIgstAmount(BigDecimal.ZERO);
        // Legacy total fields drive grandTotal = amount + gstAmount in the service.
        note.setAmount(taxable);
        note.setGstAmount(cgst.add(sgst));

        creditNoteService.create(note);

        // ============================================================
        // LEDGER: the sale and its output GST are reversed (net zero).
        // ============================================================
        TrialBalanceResponse tb =
                trialBalanceService.generateTrialBalance(DOC_DATE, DOC_DATE);

        // Window still balances overall.
        assertWithinEpsilon(tb.getTotalDebit(), tb.getTotalCredit(),
                "Invoice + credit-note window must still balance");
        assertTrue(tb.isBalanced(), "Window must be flagged balanced");

        // Sales: credited by the sale (taxable), debited by the return (taxable)
        //        -> net zero.
        TrialBalanceRow sales = row(tb, AccountingPostingService.SALES);
        assertWithinEpsilon(nz(sales.getCredit()), taxable,
                "Sales should have been credited by the original sale");
        assertWithinEpsilon(nz(sales.getDebit()), taxable,
                "Sales should have been DEBITED by the credit note (reversal)");
        assertWithinEpsilon(nz(sales.getCredit()).subtract(nz(sales.getDebit())),
                BigDecimal.ZERO, "Sales must net to zero after a full return");

        // Output CGST / SGST: credited by sale, debited by return -> net zero.
        TrialBalanceRow outCgst = row(tb, AccountingPostingService.OUTPUT_CGST);
        TrialBalanceRow outSgst = row(tb, AccountingPostingService.OUTPUT_SGST);
        assertWithinEpsilon(nz(outCgst.getDebit()), cgst,
                "Output CGST must be debited (reversed) by the credit note");
        assertWithinEpsilon(nz(outSgst.getDebit()), sgst,
                "Output SGST must be debited (reversed) by the credit note");
        assertWithinEpsilon(nz(outCgst.getCredit()).subtract(nz(outCgst.getDebit())),
                BigDecimal.ZERO, "Output CGST must net to zero after a full return");
        assertWithinEpsilon(nz(outSgst.getCredit()).subtract(nz(outSgst.getDebit())),
                BigDecimal.ZERO, "Output SGST must net to zero after a full return");

        // Debtor: debited by sale (grandTotal), credited by return (grandTotal)
        //         -> net receivable zero.
        TrialBalanceRow debtor = row(tb, customerName);
        assertWithinEpsilon(nz(debtor.getDebit()), grandTotal,
                "Debtor should have been debited the grand total by the sale");
        assertWithinEpsilon(nz(debtor.getCredit()), grandTotal,
                "Debtor should have been credited the grand total by the return");
        assertWithinEpsilon(nz(debtor.getDebit()).subtract(nz(debtor.getCredit())),
                BigDecimal.ZERO, "Debtor receivable must net to zero after a full return");

        // ============================================================
        // RECEIVABLE: the referenced invoice's outstanding is reduced.
        // ============================================================
        Invoice reloaded = invoiceRepository
                .findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new AssertionError("Invoice vanished"));

        assertWithinEpsilon(reloaded.getOutstandingAmount(), BigDecimal.ZERO,
                "A full credit note must reduce the invoice outstanding to zero");
        assertWithinEpsilon(reloaded.getPaidAmount(), grandTotal,
                "A full credit note must credit the whole invoice value");
        assertEquals("PAID", reloaded.getPaymentStatus(),
                "Fully-returned invoice must read PAID");
    }

    // ------------------------------------------------------------------

    private static TrialBalanceRow row(TrialBalanceResponse tb, String accountName) {
        return tb.getRows().stream()
                .filter(r -> accountName.equals(r.getAccountName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Expected account '" + accountName + "' in the window"));
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
