package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Bom;
import com.ledger.ledgerworks.entity.BomItem;
import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.entity.ProductionEntry;
import com.ledger.ledgerworks.entity.ProductionOutput;
import com.ledger.ledgerworks.entity.ProductionRawMaterial;

import com.ledger.ledgerworks.repository.BomRepository;
import com.ledger.ledgerworks.repository.ItemMasterRepository;
import com.ledger.ledgerworks.repository.ProductionEntryRepository;
import com.ledger.ledgerworks.repository.ProductionOutputRepository;
import com.ledger.ledgerworks.repository.ProductionRawMaterialRepository;
import com.ledger.ledgerworks.repository.ProductionScrapRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionService {

    private final ProductionEntryRepository productionRepository;

    private final ProductionOutputRepository outputRepository;

    private final ProductionRawMaterialRepository rawMaterialRepository;

    private final ProductionScrapRepository scrapRepository;

    private final ItemMasterRepository itemRepository;

    private final BomRepository bomRepository;

    private final DocumentNumberService documentNumberService;

    public ProductionService(

            ProductionEntryRepository productionRepository,

            ProductionOutputRepository outputRepository,

            ProductionRawMaterialRepository rawMaterialRepository,

            ProductionScrapRepository scrapRepository,

            ItemMasterRepository itemRepository,

            BomRepository bomRepository,

            DocumentNumberService documentNumberService
    ) {

        this.productionRepository = productionRepository;

        this.outputRepository = outputRepository;

        this.rawMaterialRepository = rawMaterialRepository;

        this.scrapRepository = scrapRepository;

        this.itemRepository = itemRepository;

        this.bomRepository = bomRepository;

        this.documentNumberService = documentNumberService;
    }

    // =====================================================
    // SAVE PRODUCTION
    // =====================================================

    public ProductionEntry save(
            ProductionEntry entry
    ) {

        // =====================================================
        // GENERATE PRODUCTION NUMBER
        // =====================================================

        ProductionEntry lastEntry =
                productionRepository
                        .findTopByProductionNumberIsNotNullOrderByIdDesc();

        String lastNumber =
                lastEntry != null
                        ? lastEntry.getProductionNumber()
                        : null;

        String productionNumber =
                documentNumberService.generateNumber(
                        "PRD",
                        lastNumber
                );

        entry.setProductionNumber(
                productionNumber
        );

        // =====================================================
        // SAVE PRODUCTION ENTRY
        // =====================================================

        ProductionEntry saved =
                productionRepository.save(entry);

        // =====================================================
        // INIT RAW MATERIAL LIST
        // =====================================================

        if (saved.getRawMaterials() == null) {

            saved.setRawMaterials(
                    new ArrayList<>()
            );
        }

        // =====================================================
        // PROCESS OUTPUTS
        // =====================================================

        if (entry.getOutputs() != null) {

            for (ProductionOutput output
                    : entry.getOutputs()) {

                // =====================================================
                // GET FINISHED GOOD ITEM
                // =====================================================

                ItemMaster fgItem =
                        itemRepository
                                .findById(
                                        output.getItem().getId()
                                )
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "Finished item not found"
                                        )
                                );

                // =====================================================
                // LINK PRODUCTION + ITEM
                // =====================================================

                output.setProductionEntry(saved);

                output.setItem(fgItem);

                // =====================================================
                // FG STOCK UPDATE
                // =====================================================

                BigDecimal fgStock =
                        fgItem.getCurrentStock() == null
                                ? BigDecimal.ZERO
                                : fgItem.getCurrentStock();

                BigDecimal fgQty =
                        output.getQuantity() == null
                                ? BigDecimal.ZERO
                                : output.getQuantity();

                fgItem.setCurrentStock(
                        fgStock.add(fgQty)
                );

                itemRepository.save(fgItem);

                // =====================================================
                // SAVE OUTPUT
                // =====================================================

                outputRepository.save(output);

                // =====================================================
                // FETCH BOM
                // =====================================================

                Bom bom =
                        bomRepository
                                .findByFinishedGoodId(
                                        fgItem.getId()
                                )
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "BOM not found for "
                                                        + fgItem.getItemName()
                                        )
                                );

                // =====================================================
                // AUTO RM CONSUMPTION
                // =====================================================

                List<ProductionRawMaterial> rmList =
                        new ArrayList<>();

                for (BomItem bomItem
                        : bom.getItems()) {

                    // =====================================================
                    // GET RM ITEM
                    // =====================================================

                    ItemMaster rmItem =
                            itemRepository
                                    .findById(
                                            bomItem.getItem().getId()
                                    )
                                    .orElseThrow(
                                            () -> new RuntimeException(
                                                    "Raw Material not found"
                                            )
                                    );

                    // =====================================================
                    // CALCULATE RM QTY
                    // =====================================================

                    BigDecimal rmQty =
                            bomItem.getQuantity()
                                    .multiply(fgQty);

                    // =====================================================
                    // STOCK VALIDATION
                    // =====================================================

                    BigDecimal currentStock =
                            rmItem.getCurrentStock() == null
                                    ? BigDecimal.ZERO
                                    : rmItem.getCurrentStock();

                    if (currentStock.compareTo(rmQty) < 0) {

                        throw new RuntimeException(
                                "Insufficient RM stock : "
                                        + rmItem.getItemName()
                        );
                    }

                    // =====================================================
                    // RM STOCK DEDUCTION
                    // =====================================================

                    rmItem.setCurrentStock(
                            currentStock.subtract(rmQty)
                    );

                    itemRepository.save(rmItem);

                    // =====================================================
                    // CREATE RM ENTRY
                    // =====================================================

                    ProductionRawMaterial rm =
                            new ProductionRawMaterial();

                    rm.setProductionEntry(saved);

                    rm.setItem(rmItem);

                    rm.setQuantity(rmQty);

                    rm.setUnit(
                            rmItem.getUnit()
                    );

                    rm.setRate(
                            rmItem.getPurchaseRate()
                    );

                    rm.setAmount(
                            rmQty.multiply(
                                    rmItem.getPurchaseRate() == null
                                            ? BigDecimal.ZERO
                                            : rmItem.getPurchaseRate()
                            )
                    );

                    // =====================================================
                    // SAVE RM ENTRY
                    // =====================================================

                    rawMaterialRepository.save(rm);

                    rmList.add(rm);
                }

                saved.getRawMaterials().addAll(rmList);
            }
        }

        return saved;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<ProductionEntry> getAll() {

        return productionRepository.findAll()
                .stream()
                .sorted((a, b) ->
                        b.getId().compareTo(a.getId())
                )
                .toList();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public ProductionEntry getById(
            Long id
    ) {

        return productionRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Production not found"
                        )
                );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Long id
    ) {

        ProductionEntry entry =
                productionRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Production not found"
                                )
                        );

        // =========================================
        // DELETE CHILD RECORDS FIRST
        // =========================================

        if (entry.getOutputs() != null) {

            outputRepository.deleteAll(
                    entry.getOutputs()
            );
        }

        if (entry.getRawMaterials() != null) {

            rawMaterialRepository.deleteAll(
                    entry.getRawMaterials()
            );
        }

        if (entry.getScraps() != null) {

            scrapRepository.deleteAll(
                    entry.getScraps()
            );
        }

        // =========================================
        // DELETE MAIN ENTRY
        // =========================================

        productionRepository.delete(entry);
    }
}