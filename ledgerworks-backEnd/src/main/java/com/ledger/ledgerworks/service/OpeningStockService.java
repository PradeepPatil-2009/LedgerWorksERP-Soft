package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.OpeningStock;
import com.ledger.ledgerworks.repository.OpeningStockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Records opening stock for an item. The opening quantity and value are
 * pushed into the live Stock + StockLedger via the existing StockService so
 * the opening balance behaves exactly like any other stock receipt.
 */
@Service
public class OpeningStockService {

    @Autowired
    private OpeningStockRepository repo;

    @Autowired
    private StockService stockService;

    // =====================================================
    // RECORD A SINGLE OPENING STOCK ENTRY
    // =====================================================

    public OpeningStock record(OpeningStock entry) {

        if (entry.getItemName() == null
                || entry.getItemName().trim().isEmpty()) {
            throw new RuntimeException("Item name is required");
        }

        entry.setItemName(entry.getItemName().trim());

        BigDecimal qty = safe(entry.getQuantity());
        entry.setQuantity(qty);

        // Derive rate from value when rate is missing (and vice versa) so the
        // ledger always has a per-unit rate to record.
        BigDecimal rate = entry.getRate();
        BigDecimal value = entry.getValue();

        if ((rate == null || rate.compareTo(BigDecimal.ZERO) == 0)
                && value != null
                && qty.compareTo(BigDecimal.ZERO) > 0) {

            rate = value.divide(qty, 2, RoundingMode.HALF_UP);
        }

        rate = safe(rate);

        if (value == null) {
            value = qty.multiply(rate);
        }

        entry.setRate(rate);
        entry.setValue(value);

        if (entry.getAsOfDate() == null) {
            entry.setAsOfDate(LocalDate.now());
        }

        OpeningStock saved = repo.save(entry);

        // Reuse existing stock posting logic: updates Stock + StockLedger.
        stockService.increaseStock(
                saved.getItemName(),
                saved.getHsnCode(),
                qty,
                rate,
                saved.getUnit()
        );

        return saved;
    }

    // =====================================================
    // BULK RECORD
    // =====================================================

    public List<OpeningStock> recordBulk(List<OpeningStock> entries) {

        List<OpeningStock> saved = new ArrayList<>();

        if (entries == null) {
            return saved;
        }

        for (OpeningStock entry : entries) {

            if (entry == null
                    || entry.getItemName() == null
                    || entry.getItemName().trim().isEmpty()) {
                continue;
            }

            saved.add(record(entry));
        }

        return saved;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<OpeningStock> getAll() {

        return repo.findAll()
                .stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .toList();
    }

    // =====================================================
    // GET BY ITEM
    // =====================================================

    public List<OpeningStock> getByItem(String itemName) {

        return repo.findByItemNameOrderByIdDesc(itemName);
    }

    // =====================================================
    // SAFE
    // =====================================================

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}
