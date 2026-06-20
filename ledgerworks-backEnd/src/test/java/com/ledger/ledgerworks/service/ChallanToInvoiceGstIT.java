package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.DeliveryChallan;
import com.ledger.ledgerworks.entity.DeliveryChallanItem;
import com.ledger.ledgerworks.entity.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression guard for the Delivery-Challan -> Invoice GST split.
 *
 * <p>A challan and the invoice converted from it must agree on the tax heads.
 * When the company GSTIN is NOT configured, {@code GstCalculatorService} falls
 * back to a name-based rule (a Maharashtra place-of-supply is intra-state).
 * {@code DeliveryChallanService} must use the SAME fallback, otherwise an
 * intra-state Maharashtra challan is wrongly treated as inter-state and — since
 * the items carry no IGST rate — the delivered invoice silently comes out with
 * ZERO GST.</p>
 *
 * <p>This reproduces exactly that scenario (company GSTIN cleared, Maharashtra
 * place-of-supply, CGST/SGST item rates, no IGST) and asserts the delivered
 * invoice carries CGST + SGST (and no IGST).</p>
 */
@SpringBootTest
@ActiveProfiles("test")
// Transactional so the delivered challan's lazy invoice/item graph stays
// navigable while we assert on the converted invoice's GST totals.
@Transactional
class ChallanToInvoiceGstIT {

    @Autowired
    private DeliveryChallanService deliveryChallanService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Test
    void maharashtraChallanWithUnconfiguredCompanyStillSplitsCgstSgst() {

        // Force the "company GSTIN not configured" path so the legacy name-based
        // fallback is exercised (deterministic regardless of test ordering).
        CompanySettings settings = companySettingsService.getSettings();
        settings.setGstNumber(null);
        companySettingsService.saveSettings(settings);

        DeliveryChallan dc = new DeliveryChallan();
        dc.setCustomerName("Challan GST Buyer " + System.nanoTime());
        dc.setPlaceOfSupply("Maharashtra"); // intra-state by the name fallback

        DeliveryChallanItem item = new DeliveryChallanItem();
        item.setDescription("Widget");
        item.setHsnCode("8888");
        item.setUnit("NOS");
        item.setQuantity(new BigDecimal("10"));
        item.setRate(new BigDecimal("100"));   // taxable = 1000
        item.setCgstPercent(new BigDecimal("9"));
        item.setSgstPercent(new BigDecimal("9"));
        item.setIgstPercent(BigDecimal.ZERO);

        List<DeliveryChallanItem> items = new ArrayList<>();
        items.add(item);
        dc.setItems(items);

        DeliveryChallan saved = deliveryChallanService.create(dc);

        // Deliver -> converts to an invoice (copies the challan's computed GST).
        DeliveryChallan delivered = deliveryChallanService.deliver(saved.getId());

        Invoice invoice = delivered.getInvoice();
        assertNotNull(invoice, "Delivering the challan must create an invoice");

        BigDecimal cgst = nz(invoice.getTotalCGST());
        BigDecimal sgst = nz(invoice.getTotalSGST());
        BigDecimal igst = nz(invoice.getTotalIGST());
        BigDecimal taxable = nz(invoice.getTotalTaxable());

        assertTrue(taxable.compareTo(new BigDecimal("999")) > 0,
                "Taxable value must carry over (~1000), got " + taxable);
        assertTrue(cgst.compareTo(BigDecimal.ZERO) > 0,
                "Intra-state Maharashtra challan must produce CGST, got " + cgst);
        assertTrue(sgst.compareTo(BigDecimal.ZERO) > 0,
                "Intra-state Maharashtra challan must produce SGST, got " + sgst);
        assertTrue(igst.compareTo(BigDecimal.ZERO) == 0,
                "Intra-state sale must have zero IGST, got " + igst);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
