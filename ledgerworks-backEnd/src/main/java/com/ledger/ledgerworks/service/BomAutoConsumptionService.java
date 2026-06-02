package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Bom;
import com.ledger.ledgerworks.entity.BomItem;
import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.entity.ProductionEntry;
import com.ledger.ledgerworks.entity.ProductionRawMaterial;

import com.ledger.ledgerworks.repository.BomRepository;
import com.ledger.ledgerworks.repository.ItemMasterRepository;
import com.ledger.ledgerworks.repository.ProductionRawMaterialRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BomAutoConsumptionService {

    private final BomRepository bomRepository;

    private final ItemMasterRepository itemRepository;

    private final ProductionRawMaterialRepository rawMaterialRepository;

    public BomAutoConsumptionService(

            BomRepository bomRepository,

            ItemMasterRepository itemRepository,

            ProductionRawMaterialRepository rawMaterialRepository
    ) {

        this.bomRepository = bomRepository;
        this.itemRepository = itemRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    // =====================================================
    // AUTO CONSUME BOM
    // =====================================================

    public void consumeBom(

            Long bomId,

            BigDecimal productionQty,

            ProductionEntry productionEntry
    ) {

        Bom bom =
                bomRepository
                        .findById(bomId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "BOM not found"
                                )
                        );

        for (BomItem bomItem
                : bom.getItems()) {

            BigDecimal requiredQty =
                    bomItem.getQuantity()
                            .multiply(productionQty);

            ItemMaster item =
                    itemRepository
                            .findById(
                                    bomItem
                                            .getItem()
                                            .getId()
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Item not found"
                                    )
                            );

            BigDecimal stock =
                    item.getCurrentStock() == null
                            ? BigDecimal.ZERO
                            : item.getCurrentStock();

            // =====================================================
            // CHECK STOCK
            // =====================================================

            if (stock.compareTo(requiredQty) < 0) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + item.getItemName()
                );
            }

            // =====================================================
            // DEDUCT STOCK
            // =====================================================

            item.setCurrentStock(
                    stock.subtract(requiredQty)
            );

            itemRepository.save(item);

            // =====================================================
            // SAVE RM CONSUMPTION
            // =====================================================

            ProductionRawMaterial rm =
                    new ProductionRawMaterial();

            rm.setProductionEntry(
                    productionEntry
            );

            rm.setItem(item);

            rm.setQuantity(requiredQty);

            rm.setUnit(item.getUnit());

            rawMaterialRepository.save(rm);
        }
    }
}