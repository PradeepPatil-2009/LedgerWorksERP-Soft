package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoiceItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class GstCalculatorService {

    private static final Logger log =
            LoggerFactory.getLogger(GstCalculatorService.class);

    private final CompanySettingsService companySettingsService;
    private final GstUtilityService gstUtilityService;

    public GstCalculatorService(CompanySettingsService companySettingsService,
                                GstUtilityService gstUtilityService) {
        this.companySettingsService = companySettingsService;
        this.gstUtilityService = gstUtilityService;
    }

    // Minimal state-name -> GST code lookup for when only a state NAME is known.
    private static final Map<String, String> NAME_TO_CODE = Map.ofEntries(
            Map.entry("jammu and kashmir", "01"),
            Map.entry("himachal pradesh", "02"),
            Map.entry("punjab", "03"),
            Map.entry("chandigarh", "04"),
            Map.entry("uttarakhand", "05"),
            Map.entry("haryana", "06"),
            Map.entry("delhi", "07"),
            Map.entry("rajasthan", "08"),
            Map.entry("uttar pradesh", "09"),
            Map.entry("bihar", "10"),
            Map.entry("sikkim", "11"),
            Map.entry("arunachal pradesh", "12"),
            Map.entry("nagaland", "13"),
            Map.entry("manipur", "14"),
            Map.entry("mizoram", "15"),
            Map.entry("tripura", "16"),
            Map.entry("meghalaya", "17"),
            Map.entry("assam", "18"),
            Map.entry("west bengal", "19"),
            Map.entry("jharkhand", "20"),
            Map.entry("odisha", "21"),
            Map.entry("chhattisgarh", "22"),
            Map.entry("madhya pradesh", "23"),
            Map.entry("gujarat", "24"),
            Map.entry("daman and diu", "25"),
            Map.entry("dadra and nagar haveli", "26"),
            Map.entry("maharashtra", "27"),
            Map.entry("karnataka", "29"),
            Map.entry("goa", "30"),
            Map.entry("lakshadweep", "31"),
            Map.entry("kerala", "32"),
            Map.entry("tamil nadu", "33"),
            Map.entry("puducherry", "34"),
            Map.entry("andaman and nicobar islands", "35"),
            Map.entry("telangana", "36"),
            Map.entry("andhra pradesh", "37"),
            Map.entry("ladakh", "38")
    );

    public void calculate(Invoice invoice) {

        BigDecimal totalTaxable = BigDecimal.ZERO;

        BigDecimal totalCGST = BigDecimal.ZERO;

        BigDecimal totalSGST = BigDecimal.ZERO;

        BigDecimal totalIGST = BigDecimal.ZERO;

        // ================= VALIDATION =================

        if (invoice.getItems() == null ||
                invoice.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Invoice must contain at least one item"
            );
        }

        // ================= PLACE-OF-SUPPLY RESOLUTION =================
        // Derive the company's own state code from its configured GSTIN, and the
        // customer's place-of-supply code, then decide intra vs inter state once.

        String companyCode = resolveCompanyStateCode();
        String customerCode = resolveCustomerStateCode(invoice);

        // intrastate when both codes are known AND equal. If the company state is
        // unconfigured we fall back to the previous name-based behaviour below.
        boolean companyConfigured = companyCode != null;
        boolean intraState;

        if (companyConfigured && customerCode != null) {
            intraState = companyCode.equals(customerCode);
        } else {
            // Fallback: previous behaviour compared the customer's state NAME to a
            // hardcoded company state. Without a configured company GSTIN we can no
            // longer trust a code comparison, so approximate the legacy behaviour by
            // treating a blank / matching-name customer state as intra-state.
            intraState = legacyIntraStateFallback(invoice);
            if (!companyConfigured) {
                log.warn("Company GSTIN/state not configured in CompanySettings; "
                        + "falling back to name-based GST split for invoice {}",
                        invoice.getInvoiceNumber());
            }
        }

        for (InvoiceItem item : invoice.getItems()) {

            if (item.getQuantity() == null ||
                    item.getRate() == null) {

                throw new IllegalArgumentException(
                        "Item quantity and rate are required"
                );
            }

            // ================= TAXABLE AMOUNT =================

            BigDecimal taxable = item.getQuantity()
                    .multiply(item.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            item.setTaxableAmount(taxable);

            totalTaxable = totalTaxable.add(taxable);

            // ================= GST RATES =================

            BigDecimal cgstRate = item.getCgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getCgstRate();

            BigDecimal sgstRate = item.getSgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getSgstRate();

            BigDecimal igstRate = item.getIgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getIgstRate();

            // ================= GST AMOUNTS =================

            BigDecimal cgstAmount = taxable
                    .multiply(cgstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal sgstAmount = taxable
                    .multiply(sgstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal igstAmount = taxable
                    .multiply(igstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            // ================= STATE LOGIC =================

            if (intraState) {

                // ================= INTRASTATE =================
                // CGST + SGST

                item.setCgstAmount(cgstAmount);

                item.setSgstAmount(sgstAmount);

                item.setIgstAmount(BigDecimal.ZERO);

                totalCGST = totalCGST.add(cgstAmount);

                totalSGST = totalSGST.add(sgstAmount);

            } else {

                // ================= INTERSTATE =================
                // IGST

                item.setIgstAmount(igstAmount);

                item.setCgstAmount(BigDecimal.ZERO);

                item.setSgstAmount(BigDecimal.ZERO);

                totalIGST = totalIGST.add(igstAmount);
            }

            // ================= LINK BACK =================

            item.setInvoice(invoice);
        }

        // ================= INVOICE TOTALS =================

        invoice.setTotalTaxable(totalTaxable);

        invoice.setTotalCGST(totalCGST);

        invoice.setTotalSGST(totalSGST);

        invoice.setTotalIGST(totalIGST);

        // ================= GRAND TOTAL =================

        BigDecimal grandTotal = totalTaxable
                .add(totalCGST)
                .add(totalSGST)
                .add(totalIGST)
                .setScale(2, RoundingMode.HALF_UP);

        invoice.setGrandTotal(grandTotal);
    }

    // =====================================================
    // COMPANY STATE CODE (from configured GSTIN)
    // =====================================================
    private String resolveCompanyStateCode() {
        try {
            CompanySettings settings = companySettingsService.getSettings();
            if (settings == null) {
                return null;
            }
            return gstUtilityService.codeFromGst(settings.getGstNumber());
        } catch (Exception ex) {
            log.warn("Could not resolve company state code: {}", ex.getMessage());
            return null;
        }
    }

    // =====================================================
    // CUSTOMER PLACE-OF-SUPPLY CODE
    // Prefer explicit stateCode, else customer GSTIN, else state name.
    // =====================================================
    private String resolveCustomerStateCode(Invoice invoice) {

        String explicit = invoice.getCustomerStateCode();
        if (explicit != null && explicit.trim().length() >= 2) {
            return explicit.trim().substring(0, 2);
        }

        String fromGst = gstUtilityService.codeFromGst(invoice.getCustomerGSTNumber());
        if (fromGst != null) {
            return fromGst;
        }

        String state = invoice.getCustomerState();
        if (state != null) {
            return NAME_TO_CODE.get(state.trim().toLowerCase());
        }

        return null;
    }

    // =====================================================
    // LEGACY FALLBACK (no company GSTIN configured)
    // Approximate the old hardcoded-Maharashtra behaviour using the
    // available state name so we never crash and never silently change
    // the split for already-configured customers.
    // =====================================================
    private boolean legacyIntraStateFallback(Invoice invoice) {

        String companyCode = resolveCompanyStateCode();
        String customerCode = resolveCustomerStateCode(invoice);

        // If both codes are now resolvable, trust them.
        if (companyCode != null && customerCode != null) {
            return companyCode.equals(customerCode);
        }

        // Otherwise, only treat as intra-state when the customer's state name
        // matches the previous default company state (Maharashtra). Anything
        // else (including a blank state) is inter-state, mirroring the prior
        // else-branch which applied IGST whenever the name did not match.
        String state = invoice.getCustomerState();
        return state != null && state.trim().equalsIgnoreCase("Maharashtra");
    }
}
