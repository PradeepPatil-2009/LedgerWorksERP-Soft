package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.entity.PurchaseItem;
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
 * DOCUMENT -> REPORT end-to-end proof for a PURCHASE.
 *
 * <p>Creates a real intra-state {@link Purchase} through {@link PurchaseService}
 * (genuine path: GST calc + stock + {@code AccountingPostingService.postPurchase}
 * in one transaction) and reads the {@link TrialBalanceService} back to prove:</p>
 *
 * <ul>
 *   <li>Input CGST + Input SGST (ASSET) are DEBITED by exactly the purchase's GST
 *       (input tax credit is an asset, so it must be a debit balance);</li>
 *   <li>the vendor / creditor is CREDITED the grandTotal;</li>
 *   <li>the Purchases (EXPENSE) account is debited the taxable value;</li>
 *   <li>the Trial Balance for the window still balances (debit == credit).</li>
 * </ul>
 *
 * <p><b>Determinism.</b> Posted on a UNIQUE far-future date and read for exactly
 * that window; the creditor account is per-vendor with a unique name. Intra-state
 * (CGST + SGST) is forced by matching the vendor GSTIN state code (27) to the
 * company GSTIN state code (27).</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class PurchasePostsInputGstIT {

    /** Isolated reporting window unique to this IT. */
    private static final LocalDate PURCHASE_DATE = LocalDate.of(2091, 7, 9);

    private static final BigDecimal EPSILON = new BigDecimal("0.005");

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private TrialBalanceService trialBalanceService;

    @Autowired
    private LedgerTransactionRepository transactionRepository;

    @Test
    void purchasePostsInputGstAsAssetDebitAndCreditsVendor() {

        // Company in Maharashtra (27) -> a vendor also in 27 is intra-state.
        CompanySettings settings = companySettingsService.getSettings();
        settings.setGstNumber("27ABCDE1234F1Z5");
        companySettingsService.saveSettings(settings);

        String vendorName = "Bharat Supplies " + uniq();

        // 5 units @ 200 = 1000 taxable; 9% CGST + 9% SGST = 90 + 90.
        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(PURCHASE_DATE);
        purchase.setVendorName(vendorName);
        purchase.setVendorGST("27ZZZZZ9999Z1Z9"); // state 27 -> intra-state

        PurchaseItem item = new PurchaseItem();
        item.setItemName("Steel Rod " + uniq());
        item.setHsnCode("7214");
        item.setQuantity(new BigDecimal("5"));
        item.setRate(new BigDecimal("200"));
        item.setCgstPercent(new BigDecimal("9"));
        item.setSgstPercent(new BigDecimal("9"));
        item.setIgstPercent(BigDecimal.ZERO);
        item.setUnit("NOS");
        purchase.getItems().add(item);

        // --- ACT: real purchase service path. ---
        Purchase saved = purchaseService.create(purchase);

        BigDecimal taxable = saved.getTotalTaxable();   // 1000.00
        BigDecimal cgst = saved.getTotalCGST();         // 90.00
        BigDecimal sgst = saved.getTotalSGST();         // 90.00
        BigDecimal igst = saved.getTotalIGST();         // 0.00
        BigDecimal grandTotal = saved.getGrandTotal();  // 1180.00

        assertWithinEpsilon(taxable, new BigDecimal("1000.00"), "purchase taxable");
        assertWithinEpsilon(cgst, new BigDecimal("90.00"), "purchase CGST");
        assertWithinEpsilon(sgst, new BigDecimal("90.00"), "purchase SGST");
        assertWithinEpsilon(igst, BigDecimal.ZERO, "purchase IGST (intra-state must be 0)");
        assertWithinEpsilon(grandTotal, new BigDecimal("1180.00"), "purchase grand total");

        // --- Ledger rows exist on the PURCHASE's own date. ---
        List<LedgerTransaction> rows = transactionRepository.findAll().stream()
                .filter(t -> PURCHASE_DATE.equals(t.getTransactionDate()))
                .toList();
        assertFalse(rows.isEmpty(),
                "Purchase must have posted ledger rows on the purchase date");
        for (LedgerTransaction t : rows) {
            assertEquals(PURCHASE_DATE, t.getTransactionDate(),
                    "Ledger row must be stamped with the purchase date, not now()");
        }

        TrialBalanceResponse tb =
                trialBalanceService.generateTrialBalance(PURCHASE_DATE, PURCHASE_DATE);

        // Balances.
        assertWithinEpsilon(tb.getTotalDebit(), tb.getTotalCredit(),
                "Trial balance for the purchase window is UNBALANCED");
        assertTrue(tb.isBalanced(),
                "TrialBalanceService must flag the purchase window balanced");
        assertWithinEpsilon(tb.getTotalDebit(), grandTotal,
                "Total debit for the window must equal the purchase grand total");

        // Input CGST / SGST are ASSET accounts and must be DEBITED by the GST.
        TrialBalanceRow inCgst = row(tb, AccountingPostingService.INPUT_CGST);
        TrialBalanceRow inSgst = row(tb, AccountingPostingService.INPUT_SGST);
        assertWithinEpsilon(nz(inCgst.getDebit()), cgst, "Input CGST debit == purchase CGST");
        assertWithinEpsilon(nz(inSgst.getDebit()), sgst, "Input SGST debit == purchase SGST");
        assertWithinEpsilon(nz(inCgst.getCredit()), BigDecimal.ZERO,
                "Input CGST must not be credited by a plain purchase");
        assertWithinEpsilon(nz(inSgst.getCredit()), BigDecimal.ZERO,
                "Input SGST must not be credited by a plain purchase");

        BigDecimal inputGst = nz(inCgst.getDebit()).add(nz(inSgst.getDebit()));
        assertWithinEpsilon(inputGst, cgst.add(sgst),
                "Input CGST + SGST debit must equal the purchase's total GST");

        // Purchases (EXPENSE) debited the taxable value.
        TrialBalanceRow purchasesRow = row(tb, AccountingPostingService.PURCHASES);
        assertWithinEpsilon(nz(purchasesRow.getDebit()), taxable,
                "Purchases debit must equal the purchase taxable value");

        // Creditor (vendor) credited the grand total.
        TrialBalanceRow creditorRow = row(tb, vendorName);
        assertWithinEpsilon(nz(creditorRow.getCredit()), grandTotal,
                "Vendor (creditor) credit must equal the purchase grand total");
        assertWithinEpsilon(nz(creditorRow.getDebit()), BigDecimal.ZERO,
                "Vendor must not be debited by the original purchase");
    }

    // ------------------------------------------------------------------

    private static TrialBalanceRow row(TrialBalanceResponse tb, String accountName) {
        return tb.getRows().stream()
                .filter(r -> accountName.equals(r.getAccountName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Expected account '" + accountName
                                + "' in the trial balance for the purchase window"));
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
