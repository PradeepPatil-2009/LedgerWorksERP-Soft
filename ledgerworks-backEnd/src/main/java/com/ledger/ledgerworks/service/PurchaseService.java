package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.entity.PurchaseItem;
import com.ledger.ledgerworks.repository.PurchaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository repo;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockLedgerService stockLedgerService;
    @Autowired
    private DocumentNumberService documentNumberService;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private GstUtilityService gstUtilityService;

    @Autowired
    private AccountingPostingService accountingPostingService;

    // Compact state-name -> GST code lookup for when a place-of-supply is
    // supplied as a NAME (or the legacy "MH") rather than a GSTIN.
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
            Map.entry("mh", "27"),
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

    // ================= CREATE =================

    @Transactional
    public Purchase create(Purchase p) {

        if (
                p.getItems() == null
                ||
                p.getItems().isEmpty()
        ) {

            throw new RuntimeException(
                    "Purchase items required"
            );
        }

        // ================= DOCUMENT DATE =================
        // Use the real document date. Only default to today when the caller
        // did not supply one; never overwrite a provided purchase date.

        if (p.getPurchaseDate() == null) {

            p.setPurchaseDate(
                    LocalDate.now()
            );
        }

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(
                p.getPurchaseDate()
        );

        Purchase lastPurchase =
                repo.findTopByOrderByIdDesc();

        String lastPurchaseNumber = null;

        if (lastPurchase != null) {

            lastPurchaseNumber =
                    lastPurchase.getPurchaseNumber();
        }

        p.setPurchaseNumber(

                documentNumberService.generateNumber(

                        "PUR",

                        lastPurchaseNumber
                )
        );

        p.setStatus("ACTIVE");

        // REMOVE EMPTY ITEMS

        p.getItems().removeIf(
                item ->
                        item.getItemName() == null
                        ||
                        item.getItemName().trim().isEmpty()
        );

        for (PurchaseItem item : p.getItems()) {

            item.setPurchase(p);
        }

        calculate(p);

        Purchase saved = repo.save(p);

        // ================= STOCK INCREASE =================

        for (PurchaseItem item : saved.getItems()) {

            stockService.increaseStock(

                    item.getItemName(),

                    item.getHsnCode(),

                    item.getQuantity(),

                    item.getRate(),

                    item.getUnit()
            );

            stockLedgerService.addPurchaseEntry(

                    item.getItemName(),

                    saved.getPurchaseNumber(),

                    item.getQuantity(),

                    item.getRate()
            );
        }

        // ================= LEDGER POSTING =================
        // Post the balanced purchase journal on the purchase's own date, inside
        // the same transaction. Best-effort: never breaks purchase creation.

        accountingPostingService.postPurchase(saved);

        return saved;
    }

    // ================= UPDATE =================

    public Purchase update(
            Long id,
            Purchase updated
    ) {

        Purchase existing = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Purchase not found"
                        )
                );

        if (
                !"ACTIVE".equals(
                        existing.getStatus()
                )
        ) {

            throw new RuntimeException(
                    "Only ACTIVE purchase editable"
            );
        }

        if (
                updated.getItems() == null
                ||
                updated.getItems().isEmpty()
        ) {

            throw new RuntimeException(
                    "Purchase items required"
            );
        }

        // ================= REVERSE OLD STOCK =================

        for (PurchaseItem oldItem :
                existing.getItems()) {

            stockService.decreaseStock(

                    oldItem.getItemName(),

                    oldItem.getQuantity(),

                    oldItem.getRate(),

                    oldItem.getUnit()
            );
        }

        // ================= HEADER UPDATE =================

        existing.setVendorName(
                updated.getVendorName()
        );

        existing.setVendorAddress(
                updated.getVendorAddress()
        );

        existing.setVendorGST(
                updated.getVendorGST()
        );

        existing.setVendorPhone(
                updated.getVendorPhone()
        );

        existing.setVendorEmail(
                updated.getVendorEmail()
        );

        existing.setPlaceOfSupply(
                updated.getPlaceOfSupply()
        );

        existing.setRemarks(
                updated.getRemarks()
        );

        // ================= CLEAR ITEMS =================

        existing.getItems().clear();

        // ================= ADD ITEMS =================

        for (PurchaseItem item :
                updated.getItems()) {

            if (
                    item.getItemName() == null
                    ||
                    item.getItemName().trim().isEmpty()
            ) {

                continue;
            }

            item.setPurchase(existing);

            existing.getItems().add(item);
        }

        calculate(existing);

        Purchase saved = repo.save(existing);

        // ================= ADD NEW STOCK =================

        for (PurchaseItem item :
                saved.getItems()) {

            stockService.increaseStock(

                    item.getItemName(),

                    item.getHsnCode(),

                    item.getQuantity(),

                    item.getRate(),

                    item.getUnit()
            );

            stockLedgerService.addPurchaseEntry(

                    item.getItemName(),

                    saved.getPurchaseNumber(),

                    item.getQuantity(),

                    item.getRate()
            );
        }

        return saved;
    }

    // ================= CANCEL =================

    public Purchase cancel(Long id) {

        Purchase p = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Purchase not found"
                        )
                );

        if (
                "CANCELLED".equals(
                        p.getStatus()
                )
        ) {

            return p;
        }

        // ================= REVERSE STOCK =================

        for (PurchaseItem item :
                p.getItems()) {

            stockService.decreaseStock(

                    item.getItemName(),

                    item.getQuantity(),

                    item.getRate(),

                    item.getUnit()
            );
        }

        p.setStatus("CANCELLED");

        return repo.save(p);
    }

    // ================= GET ALL =================

    public List<Purchase> getAll() {

        return repo.findAll();
    }

    // ================= GET BY ID =================

    public Purchase getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Purchase not found"
                        )
                );
    }

    // ================= GST CALCULATION =================

    private void calculate(Purchase p) {

        BigDecimal totalTaxable =
                BigDecimal.ZERO;

        BigDecimal totalCGST =
                BigDecimal.ZERO;

        BigDecimal totalSGST =
                BigDecimal.ZERO;

        BigDecimal totalIGST =
                BigDecimal.ZERO;

        boolean intraState =
                isIntraState(p);

        for (PurchaseItem item :
                p.getItems()) {

            BigDecimal qty =
                    safe(item.getQuantity());

            BigDecimal rate =
                    safe(item.getRate());

            BigDecimal taxable =
                    qty.multiply(rate);

            BigDecimal cgst =
                    BigDecimal.ZERO;

            BigDecimal sgst =
                    BigDecimal.ZERO;

            BigDecimal igst =
                    BigDecimal.ZERO;

            if (intraState) {

                cgst = taxable.multiply(
                                safe(item.getCgstPercent())
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

                sgst = taxable.multiply(
                                safe(item.getSgstPercent())
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

            } else {

                igst = taxable.multiply(
                                safe(item.getIgstPercent())
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );
            }

            BigDecimal total =
                    taxable
                            .add(cgst)
                            .add(sgst)
                            .add(igst);

            item.setTaxableAmount(taxable);

            item.setCgstAmount(cgst);

            item.setSgstAmount(sgst);

            item.setIgstAmount(igst);

            item.setTotalAmount(total);

            totalTaxable =
                    totalTaxable.add(taxable);

            totalCGST =
                    totalCGST.add(cgst);

            totalSGST =
                    totalSGST.add(sgst);

            totalIGST =
                    totalIGST.add(igst);
        }

        p.setTotalTaxable(totalTaxable);

        p.setTotalCGST(totalCGST);

        p.setTotalSGST(totalSGST);

        p.setTotalIGST(totalIGST);

        p.setGrandTotal(

                totalTaxable
                        .add(totalCGST)
                        .add(totalSGST)
                        .add(totalIGST)
        );
    }

    // ================= STATE / GST HEAD RESOLUTION =================
    // Mirror the invoice foundation: derive the company's own state code from
    // its configured GSTIN and compare it to the vendor's place-of-supply code.
    // CGST + SGST when both codes are known AND equal (intra-state); IGST
    // otherwise (inter-state, or when either side's state cannot be resolved).

    private boolean isIntraState(Purchase p) {

        String companyCode = resolveCompanyStateCode();
        String vendorCode = resolveVendorStateCode(p);

        if (companyCode != null && vendorCode != null) {
            return companyCode.equals(vendorCode);
        }

        return false;
    }

    // Company's own state code from the configured GSTIN.
    private String resolveCompanyStateCode() {

        try {

            CompanySettings settings =
                    companySettingsService.getSettings();

            if (settings == null) {
                return null;
            }

            return gstUtilityService.codeFromGst(
                    settings.getGstNumber()
            );

        } catch (Exception ex) {
            return null;
        }
    }

    // Vendor place-of-supply code: prefer the vendor GSTIN, then a GSTIN-like
    // place-of-supply, then a state name (including the legacy "MH").
    private String resolveVendorStateCode(Purchase p) {

        String fromGst =
                gstUtilityService.codeFromGst(p.getVendorGST());

        if (fromGst != null) {
            return fromGst;
        }

        String place = p.getPlaceOfSupply();

        if (place == null) {
            return null;
        }

        String trimmed = place.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        // A GSTIN-like place of supply (starts with 2 digits).
        if (trimmed.length() >= 2
                && Character.isDigit(trimmed.charAt(0))
                && Character.isDigit(trimmed.charAt(1))) {

            return trimmed.substring(0, 2);
        }

        return NAME_TO_CODE.get(trimmed.toLowerCase());
    }

    // ================= SAFE =================

    private BigDecimal safe(
            BigDecimal val
    ) {

        return val != null
                ? val
                : BigDecimal.ZERO;
    }
}