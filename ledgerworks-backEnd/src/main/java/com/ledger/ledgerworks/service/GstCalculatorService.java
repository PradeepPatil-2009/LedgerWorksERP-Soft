package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoiceItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GstCalculatorService {

    private static final String COMPANY_STATE = "Maharashtra";

    public void calculate(Invoice invoice) {

        BigDecimal totalTaxable = BigDecimal.ZERO;

        BigDecimal totalCGST = BigDecimal.ZERO;

        BigDecimal totalSGST = BigDecimal.ZERO;

        BigDecimal totalIGST = BigDecimal.ZERO;

        // ================= VALIDATION =================

        if (invoice.getItems() == null ||
                invoice.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Invoice must contain at least one item"
            );
        }

        for (InvoiceItem item : invoice.getItems()) {

            if (item.getQuantity() == null ||
                    item.getRate() == null) {

                throw new IllegalArgumentException(
                        "Item quantity and rate are required"
                );
            }

            // ================= TAXABLE AMOUNT =================

            BigDecimal taxable = item.getQuantity()
                    .multiply(item.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            item.setTaxableAmount(taxable);

            totalTaxable = totalTaxable.add(taxable);

            // ================= GST RATES =================

            BigDecimal cgstRate = item.getCgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getCgstRate();

            BigDecimal sgstRate = item.getSgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getSgstRate();

            BigDecimal igstRate = item.getIgstRate() == null
                    ? BigDecimal.ZERO
                    : item.getIgstRate();

            // ================= GST AMOUNTS =================

            BigDecimal cgstAmount = taxable
                    .multiply(cgstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal sgstAmount = taxable
                    .multiply(sgstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal igstAmount = taxable
                    .multiply(igstRate)
                    .divide(BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

            // ================= STATE LOGIC =================

            String customerState = invoice.getCustomerState();

            if (customerState != null &&
                    customerState.trim()
                            .equalsIgnoreCase(COMPANY_STATE)) {

                // ================= INTRASTATE =================
                // CGST + SGST

                item.setCgstAmount(cgstAmount);

                item.setSgstAmount(sgstAmount);

                item.setIgstAmount(BigDecimal.ZERO);

                totalCGST = totalCGST.add(cgstAmount);

                totalSGST = totalSGST.add(sgstAmount);

            } else {

                // ================= INTERSTATE =================
                // IGST

                item.setIgstAmount(igstAmount);

                item.setCgstAmount(BigDecimal.ZERO);

                item.setSgstAmount(BigDecimal.ZERO);

                totalIGST = totalIGST.add(igstAmount);
            }

            // ================= LINK BACK =================

            item.setInvoice(invoice);
        }

        // ================= INVOICE TOTALS =================

        invoice.setTotalTaxable(totalTaxable);

        invoice.setTotalCGST(totalCGST);

        invoice.setTotalSGST(totalSGST);

        invoice.setTotalIGST(totalIGST);

        // ================= GRAND TOTAL =================

        BigDecimal grandTotal = totalTaxable
                .add(totalCGST)
                .add(totalSGST)
                .add(totalIGST)
                .setScale(2, RoundingMode.HALF_UP);

        invoice.setGrandTotal(grandTotal);
    }
}