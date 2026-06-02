package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.entity.MaterialIssue;
import com.ledger.ledgerworks.entity.MaterialIssueItem;
import com.ledger.ledgerworks.repository.ItemMasterRepository;
import com.ledger.ledgerworks.repository.MaterialIssueItemRepository;
import com.ledger.ledgerworks.repository.MaterialIssueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MaterialIssueService {

    private final MaterialIssueRepository issueRepository;

    private final MaterialIssueItemRepository itemRepository;

    private final ItemMasterRepository masterRepository;
    
    @Autowired
    private StockService stockService;

    public MaterialIssueService(
            MaterialIssueRepository issueRepository,
            MaterialIssueItemRepository itemRepository,
            ItemMasterRepository masterRepository
    ) {

        this.issueRepository = issueRepository;
        this.itemRepository = itemRepository;
        this.masterRepository = masterRepository;
    }

    // =====================================================
    // CREATE MATERIAL ISSUE
    // =====================================================

    public MaterialIssue create(
            MaterialIssue issue
    ) {

        // =====================================================
        // SAVE ISSUE HEADER
        // =====================================================

        MaterialIssue saved =
                issueRepository.save(issue);

        // =====================================================
        // PROCESS ISSUE ITEMS
        // =====================================================

        if (issue.getItems() != null) {

            for (MaterialIssueItem item
                    : issue.getItems()) {

                // =====================================================
                // LINK ISSUE
                // =====================================================

                item.setMaterialIssue(saved);

                // =====================================================
                // FETCH ITEM MASTER
                // =====================================================

                ItemMaster stockItem =
                        masterRepository
                                .findById(
                                        item.getItem().getId()
                                )
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "Item not found"
                                        )
                                );

                // =====================================================
                // SET FULL ITEM OBJECT
                // =====================================================

                item.setItem(stockItem);

                // =====================================================
                // CURRENT STOCK
                // =====================================================

                BigDecimal currentStock =
                        stockItem.getCurrentStock() == null
                                ? BigDecimal.ZERO
                                : stockItem.getCurrentStock();

                // =====================================================
                // ISSUE QUANTITY
                // =====================================================

                BigDecimal issueQty =
                        item.getQuantity() == null
                                ? BigDecimal.ZERO
                                : item.getQuantity();

                // =====================================================
                // STOCK VALIDATION
                // =====================================================

                if (currentStock.compareTo(issueQty) < 0) {

                    throw new RuntimeException(
                            "Insufficient stock for item : "
                                    + stockItem.getItemName()
                    );
                }

                // =====================================================
                // AUTO UNIT SET
                // =====================================================

                item.setUnit(
                        stockItem.getUnit()
                );

                // =====================================================
                // AUTO RATE SET
                // =====================================================

                if (item.getRate() == null) {

                    item.setRate(
                            stockItem.getPurchaseRate()
                    );
                }

                // =====================================================
                // AUTO AMOUNT CALCULATION
                // =====================================================

                BigDecimal rate =
                        item.getRate() == null
                                ? BigDecimal.ZERO
                                : item.getRate();

                item.setAmount(
                        rate.multiply(issueQty)
                );

                // =====================================================
                // DEDUCT STOCK
                // =====================================================

                stockItem.setCurrentStock(
                        currentStock.subtract(issueQty)
                );

                // =====================================================
                // SAVE UPDATED STOCK
                // =====================================================

                masterRepository.save(stockItem);

                // =====================================================
                // SAVE ISSUE ITEM
                // =====================================================
                
                stockService.decreaseStock(

                        stockItem.getItemName(),

                        issueQty,

                        item.getRate(),

                        stockItem.getUnit()
                );

                itemRepository.save(item);
            }
        }

        // =====================================================
        // RETURN SAVED ISSUE
        // =====================================================

        return issueRepository.findById(
                saved.getId()
        ).orElseThrow(
                () -> new RuntimeException(
                        "Material Issue not found"
                )
        );
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<MaterialIssue> getAll() {

        return issueRepository.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public MaterialIssue getById(
            Long id
    ) {

        return issueRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Material Issue not found"
                        )
                );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Long id
    ) {

        issueRepository.deleteById(id);
    }
}