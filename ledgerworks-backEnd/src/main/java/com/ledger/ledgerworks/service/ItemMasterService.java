package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.repository.ItemMasterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemMasterService {

    @Autowired
    private ItemMasterRepository repo;

    @Autowired
    private StockService stockService;

    // =====================================================
    // CREATE ITEM
    // =====================================================

    public ItemMaster create(
            ItemMaster item
    ) {

        // =====================================================
        // DUPLICATE CHECK
        // =====================================================

        if (repo.existsByItemName(
                item.getItemName()
        )) {

            throw new RuntimeException(
                    "Item already exists"
            );
        }

        // =====================================================
        // AUTO ITEM CODE
        // =====================================================

        item.setItemCode(
                "ITM-" + System.currentTimeMillis()
        );

        // =====================================================
        // STATUS
        // =====================================================

        item.setStatus("ACTIVE");

        // =====================================================
        // NULL STOCK HANDLING
        // =====================================================

        if (item.getCurrentStock() == null) {

            item.setCurrentStock(
                    BigDecimal.ZERO
            );
        }

        // =====================================================
        // NULL PURCHASE RATE
        // =====================================================

        if (item.getPurchaseRate() == null) {

            item.setPurchaseRate(
                    BigDecimal.ZERO
            );
        }

        // =====================================================
        // NULL SALES RATE
        // =====================================================

        if (item.getSaleRate() == null) {

            item.setSaleRate(
                    BigDecimal.ZERO
            );
        }

        // =====================================================
        // SAVE ITEM
        // =====================================================

        ItemMaster savedItem =
                repo.save(item);

        // =====================================================
        // CREATE STOCK ENTRY
        // =====================================================

        stockService.increaseStock(

                savedItem.getItemName(),

                savedItem.getHsnCode(),

                savedItem.getCurrentStock(),

                savedItem.getPurchaseRate(),

                savedItem.getUnit()
        );

        return savedItem;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<ItemMaster> getAll() {

        return repo.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public ItemMaster getById(
            Long id
    ) {

        return repo.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Item not found"
                        )
                );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Long id
    ) {

        repo.deleteById(id);
    }
}