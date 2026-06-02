package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ProductionEntry;
import com.ledger.ledgerworks.entity.ProductionOutput;
import com.ledger.ledgerworks.entity.ProductionRawMaterial;
import com.ledger.ledgerworks.repository.ProductionEntryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProductionCostService {

    @Autowired
    private ProductionEntryRepository productionRepo;

    // =====================================================
    // CALCULATE RAW MATERIAL COST
    // =====================================================

    public BigDecimal calculateRawMaterialCost(

            List<ProductionRawMaterial> materials
    ) {

        BigDecimal total =
                BigDecimal.ZERO;

        if (materials == null
                || materials.isEmpty()) {

            return total;
        }

        for (ProductionRawMaterial rm
                : materials) {

            BigDecimal qty =
                    rm.getQuantity() == null
                            ? BigDecimal.ZERO
                            : rm.getQuantity();

            BigDecimal rate =
                    rm.getItem() == null
                            || rm.getItem()
                            .getPurchaseRate() == null

                            ? BigDecimal.ZERO

                            : rm.getItem()
                            .getPurchaseRate();

            BigDecimal amount =
                    qty.multiply(rate);

            total =
                    total.add(amount);

            rm.setRate(rate);

            rm.setAmount(amount);
        }

        return total;
    }

    // =====================================================
    // CALCULATE PRODUCTION QTY
    // =====================================================

    public BigDecimal calculateProductionQty(

            List<ProductionOutput> outputs
    ) {

        BigDecimal totalQty =
                BigDecimal.ZERO;

        if (outputs == null
                || outputs.isEmpty()) {

            return totalQty;
        }

        for (ProductionOutput output
                : outputs) {

            BigDecimal qty =
                    output.getQuantity() == null
                            ? BigDecimal.ZERO
                            : output.getQuantity();

            totalQty =
                    totalQty.add(qty);
        }

        return totalQty;
    }

    // =====================================================
    // CALCULATE TOTAL COST
    // =====================================================

    public BigDecimal calculateTotalCost(

            BigDecimal materialCost,

            BigDecimal labourCost,

            BigDecimal overheadCost
    ) {

        materialCost =
                materialCost == null
                        ? BigDecimal.ZERO
                        : materialCost;

        labourCost =
                labourCost == null
                        ? BigDecimal.ZERO
                        : labourCost;

        overheadCost =
                overheadCost == null
                        ? BigDecimal.ZERO
                        : overheadCost;

        return materialCost
                .add(labourCost)
                .add(overheadCost);
    }

    // =====================================================
    // CALCULATE COST PER UNIT
    // =====================================================

    public BigDecimal calculateCostPerUnit(

            BigDecimal totalCost,

            BigDecimal productionQty
    ) {

        if (productionQty == null
                || productionQty.compareTo(
                BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return totalCost.divide(

                productionQty,

                2,

                RoundingMode.HALF_UP
        );
    }

    // =====================================================
    // CALCULATE FULL COST
    // =====================================================

    public ProductionCostSummary calculateCost(

            Long productionId
    ) {

        ProductionEntry entry =
                productionRepo
                        .findById(productionId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Production not found"
                                )
                        );

        BigDecimal materialCost =
                calculateRawMaterialCost(
                        entry.getRawMaterials()
                );

        BigDecimal labourCost =
                new BigDecimal("100");

        BigDecimal overheadCost =
                new BigDecimal("50");

        BigDecimal totalCost =
                calculateTotalCost(

                        materialCost,

                        labourCost,

                        overheadCost
                );

        BigDecimal productionQty =
                calculateProductionQty(
                        entry.getOutputs()
                );

        BigDecimal costPerUnit =
                calculateCostPerUnit(

                        totalCost,

                        productionQty
                );

        return new ProductionCostSummary(

                materialCost,

                labourCost,

                overheadCost,

                totalCost,

                productionQty,

                costPerUnit
        );
    }

    // =====================================================
    // GET BY PRODUCTION
    // =====================================================

    public ProductionCostSummary getByProduction(

            Long productionId
    ) {

        return calculateCost(productionId);
    }

    // =====================================================
    // DTO CLASS
    // =====================================================

    public static class ProductionCostSummary {

        private BigDecimal materialCost;

        private BigDecimal labourCost;

        private BigDecimal overheadCost;

        private BigDecimal totalCost;

        private BigDecimal productionQty;

        private BigDecimal costPerUnit;

        public ProductionCostSummary(
                BigDecimal materialCost,
                BigDecimal labourCost,
                BigDecimal overheadCost,
                BigDecimal totalCost,
                BigDecimal productionQty,
                BigDecimal costPerUnit
        ) {

            this.materialCost = materialCost;
            this.labourCost = labourCost;
            this.overheadCost = overheadCost;
            this.totalCost = totalCost;
            this.productionQty = productionQty;
            this.costPerUnit = costPerUnit;
        }

        public BigDecimal getMaterialCost() {
            return materialCost;
        }

        public BigDecimal getLabourCost() {
            return labourCost;
        }

        public BigDecimal getOverheadCost() {
            return overheadCost;
        }

        public BigDecimal getTotalCost() {
            return totalCost;
        }

        public BigDecimal getProductionQty() {
            return productionQty;
        }

        public BigDecimal getCostPerUnit() {
            return costPerUnit;
        }
    }
}