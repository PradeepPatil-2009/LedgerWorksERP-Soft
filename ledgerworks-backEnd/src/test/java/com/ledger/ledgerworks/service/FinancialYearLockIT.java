package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.FinancialYear;
import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.entity.PurchaseItem;
import com.ledger.ledgerworks.repository.FinancialYearRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the FINANCIAL-YEAR LOCK now covers non-invoice document paths.
 *
 * <p>Historically only the invoice flow called {@code assertOpen}; the fix wires
 * the same guard into the other document services. This IT locks a dedicated
 * far-future financial year and then asserts that {@link PurchaseService#create}
 * — a NON-invoice path — is REJECTED for a date inside the locked window, while a
 * date just outside it still succeeds. That distinguishes a real lock from a
 * blanket failure.</p>
 *
 * <p>The locked window is a unique far-future year so it cannot overlap the seed
 * financial year (the current April–March) or the windows used by the other
 * ITs.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class FinancialYearLockIT {

    // A dedicated, far-future locked year.
    private static final LocalDate LOCK_START = LocalDate.of(2093, 4, 1);
    private static final LocalDate LOCK_END = LocalDate.of(2094, 3, 31);

    // Inside the locked window -> must be rejected.
    private static final LocalDate INSIDE_LOCK = LocalDate.of(2093, 9, 15);

    // Just outside the locked window (the day after it ends) -> must be allowed.
    private static final LocalDate OUTSIDE_LOCK = LocalDate.of(2094, 4, 1);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Test
    void purchaseDatedInsideALockedFinancialYearIsRejected() {

        // --- Lock a dedicated far-future financial year. ---
        FinancialYear fy = new FinancialYear();
        fy.setName("FY-LOCK-" + uniq());
        fy.setStartDate(LOCK_START);
        fy.setEndDate(LOCK_END);
        fy.setActive(false);
        fy.setLocked(true);
        financialYearRepository.save(fy);

        // --- A purchase dated INSIDE the locked window must be rejected. The
        //     guard throws IllegalStateException ("Financial year is locked"). ---
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> purchaseService.create(buildPurchase(INSIDE_LOCK)),
                "Creating a purchase inside a LOCKED financial year must be rejected "
                        + "(assertOpen must fire on the non-invoice path)");

        assertTrue(ex.getMessage() != null
                        && ex.getMessage().toLowerCase().contains("locked"),
                "Rejection must come from the financial-year lock, got: "
                        + ex.getMessage());

        // --- A purchase dated OUTSIDE the locked window must still succeed,
        //     proving the lock is scoped to the window, not a blanket failure. ---
        Purchase ok = purchaseService.create(buildPurchase(OUTSIDE_LOCK));
        assertNotNull(ok.getId(),
                "A purchase outside the locked window must save normally");
        assertNotNull(ok.getPurchaseNumber(),
                "A successful purchase must receive a document number");
    }

    private Purchase buildPurchase(LocalDate date) {
        Purchase p = new Purchase();
        p.setPurchaseDate(date);
        p.setVendorName("Lock Test Vendor " + uniq());

        PurchaseItem item = new PurchaseItem();
        item.setItemName("Locked-FY Item " + uniq());
        item.setHsnCode("1234");
        item.setQuantity(new BigDecimal("1"));
        item.setRate(new BigDecimal("100"));
        item.setCgstPercent(BigDecimal.ZERO);
        item.setSgstPercent(BigDecimal.ZERO);
        item.setIgstPercent(BigDecimal.ZERO);
        item.setUnit("NOS");
        p.getItems().add(item);
        return p;
    }

    private static long uniq() {
        return System.nanoTime();
    }
}
