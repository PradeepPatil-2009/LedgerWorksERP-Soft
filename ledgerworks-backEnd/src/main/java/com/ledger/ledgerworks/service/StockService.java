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
public class StockService {

    @Autowired
    private StockRepository repo;

    @Autowired
    private StockLedgerRepository stockLedgerRepository;

    // ================= INCREASE STOCK =================

    public void increaseStock(
            String itemName,
            String hsnCode,
            BigDecimal qty,
            BigDecimal rate,
            String unit
    ) {

        Stock stock = repo.findByItemName(itemName)
                .orElse(new Stock());

        stock.setItemName(itemName);

        stock.setHsnCode(hsnCode);

        stock.setUnit(unit);

        stock.setPurchaseQty(
                safe(stock.getPurchaseQty())
                        .add(safe(qty))
        );

        stock.setAvailableQty(
                safe(stock.getAvailableQty())
                        .add(safe(qty))
        );

        stock.setLastPurchaseRate(rate);

        stock.setStatus("ACTIVE");

        repo.save(stock);

        // ================= STOCK LEDGER =================

        StockLedger ledger =
                new StockLedger();

        ledger.setEntryDate(
                LocalDate.now()
        );

        ledger.setItemName(itemName);

        ledger.setTransactionType(
                "PURCHASE"
        );

        ledger.setReferenceNo(
                "PURCHASE"
        );

        ledger.setQtyIn(qty);

        ledger.setQtyOut(
                BigDecimal.ZERO
        );

        ledger.setBalanceQty(
                stock.getAvailableQty()
        );

        ledger.setRate(rate);

        ledger.setRemarks(
                "Stock Increased"
        );

        stockLedgerRepository.save(ledger);
    }

    // ================= DECREASE STOCK =================

    public void decreaseStock(
            String itemName,
            BigDecimal qty,
            BigDecimal rate,
            String unit
    ) {

        Stock stock = repo.findByItemName(itemName)
                .orElseThrow(() ->

                        new RuntimeException(
                                "Stock not found for item: "
                                        + itemName
                        )
                );

        BigDecimal available =
                safe(stock.getAvailableQty());

        if (available.compareTo(
                safe(qty)
        ) < 0) {

            throw new RuntimeException(
                    "Insufficient stock for item: "
                            + itemName
            );
        }

        stock.setSalesQty(
                safe(stock.getSalesQty())
                        .add(safe(qty))
        );

        stock.setAvailableQty(
                available.subtract(
                        safe(qty)
                )
        );

        stock.setLastSalesRate(rate);

        stock.setUnit(unit);

        repo.save(stock);

        // ================= STOCK LEDGER =================

        StockLedger ledger =
                new StockLedger();

        ledger.setEntryDate(
                LocalDate.now()
        );

        ledger.setItemName(itemName);

        ledger.setTransactionType(
                "SALE"
        );

        ledger.setReferenceNo(
                "INVOICE"
        );

        ledger.setQtyIn(
                BigDecimal.ZERO
        );

        ledger.setQtyOut(qty);

        ledger.setBalanceQty(
                stock.getAvailableQty()
        );

        ledger.setRate(rate);

        ledger.setRemarks(
                "Stock Decreased"
        );

        stockLedgerRepository.save(ledger);
    }

    // ================= GET ALL =================

    public List<Stock> getAll() {

        return repo.findAll();
    }

    // ================= LOW STOCK =================

    public List<Stock> getLowStock() {

        return repo.findByAvailableQtyLessThan(
                BigDecimal.TEN
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