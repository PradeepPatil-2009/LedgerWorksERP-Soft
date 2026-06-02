package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.entity.PurchaseItem;
import com.ledger.ledgerworks.repository.PurchaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

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

    // ================= CREATE =================

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

        p.setPurchaseDate(
                LocalDate.now()
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
                "MH".equalsIgnoreCase(
                        p.getPlaceOfSupply()
                );

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

    // ================= SAFE =================

    private BigDecimal safe(
            BigDecimal val
    ) {

        return val != null
                ? val
                : BigDecimal.ZERO;
    }
}