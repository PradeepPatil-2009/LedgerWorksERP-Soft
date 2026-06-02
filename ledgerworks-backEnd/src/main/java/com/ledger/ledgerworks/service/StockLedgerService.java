package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Stock;
import com.ledger.ledgerworks.entity.StockLedger;
import com.ledger.ledgerworks.repository.StockLedgerRepository;
import com.ledger.ledgerworks.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockLedgerService {

    @Autowired
    private StockLedgerRepository repo;

    @Autowired
    private StockRepository stockRepo;

    // =====================================================
    // PURCHASE ENTRY
    // =====================================================

    public void addPurchaseEntry(

            String itemName,

            String referenceNo,

            BigDecimal qty,

            BigDecimal rate
    ) {

        saveLedger(

                itemName,

                "PURCHASE",

                referenceNo,

                qty,

                BigDecimal.ZERO,

                rate,

                "Purchase Entry"
        );
    }

    // =====================================================
    // SALES ENTRY
    // =====================================================

    public void addSalesEntry(

            String itemName,

            String referenceNo,

            BigDecimal qty,

            BigDecimal rate
    ) {

        saveLedger(

                itemName,

                "SALE",

                referenceNo,

                BigDecimal.ZERO,

                qty,

                rate,

                "Sales Entry"
        );
    }

    // =====================================================
    // MATERIAL ISSUE ENTRY
    // =====================================================

    public void addMaterialIssueEntry(

            String itemName,

            String referenceNo,

            BigDecimal qty,

            BigDecimal rate
    ) {

        saveLedger(

                itemName,

                "MATERIAL_ISSUE",

                referenceNo,

                BigDecimal.ZERO,

                qty,

                rate,

                "Material Issue Entry"
        );
    }

    // =====================================================
    // PRODUCTION ENTRY
    // =====================================================

    public void addProductionEntry(

            String itemName,

            String referenceNo,

            BigDecimal qty,

            BigDecimal rate
    ) {

        saveLedger(

                itemName,

                "PRODUCTION",

                referenceNo,

                qty,

                BigDecimal.ZERO,

                rate,

                "Production Entry"
        );
    }

    // =====================================================
    // BOM CONSUMPTION ENTRY
    // =====================================================

    public void addBomConsumptionEntry(

            String itemName,

            String referenceNo,

            BigDecimal qty,

            BigDecimal rate
    ) {

        saveLedger(

                itemName,

                "BOM_CONSUMPTION",

                referenceNo,

                BigDecimal.ZERO,

                qty,

                rate,

                "BOM Consumption Entry"
        );
    }

    // =====================================================
    // COMMON LEDGER SAVE METHOD
    // =====================================================

    private void saveLedger(

            String itemName,

            String transactionType,

            String referenceNo,

            BigDecimal qtyIn,

            BigDecimal qtyOut,

            BigDecimal rate,

            String remarks
    ) {

        Stock stock = stockRepo
                .findByItemName(itemName)
                .orElseGet(Stock::new);

        BigDecimal balanceQty =
                stock.getAvailableQty() == null
                        ? BigDecimal.ZERO
                        : stock.getAvailableQty();

        StockLedger ledger =
                new StockLedger();

        ledger.setEntryDate(
                LocalDate.now()
        );

        ledger.setItemName(
                itemName
        );

        ledger.setTransactionType(
                transactionType
        );

        ledger.setReferenceNo(
                referenceNo
        );

        ledger.setQtyIn(
                qtyIn == null
                        ? BigDecimal.ZERO
                        : qtyIn
        );

        ledger.setQtyOut(
                qtyOut == null
                        ? BigDecimal.ZERO
                        : qtyOut
        );

        ledger.setBalanceQty(
                balanceQty
        );

        ledger.setRate(
                rate == null
                        ? BigDecimal.ZERO
                        : rate
        );

        ledger.setRemarks(
                remarks
        );

        repo.save(ledger);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<StockLedger> getAll() {

        return repo.findAll()
                .stream()
                .sorted(
                        (a, b) ->
                                b.getId()
                                        .compareTo(
                                                a.getId()
                                        )
                )
                .toList();
    }

    // =====================================================
    // ITEM WISE
    // =====================================================

    public List<StockLedger> getByItem(
            String itemName
    ) {

        return repo
                .findByItemNameOrderByIdDesc(
                        itemName
                );
    }
}