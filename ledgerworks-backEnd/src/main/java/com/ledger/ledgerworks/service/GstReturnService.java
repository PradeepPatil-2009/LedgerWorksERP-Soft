package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.Gstr1Row;
import com.ledger.ledgerworks.dto.Gstr3bSummary;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoiceItem;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only GSTR reports (GSTR-1 / GSTR-3B) built from outward-supply invoices.
 */
@Service
public class GstReturnService {

    private final InvoiceRepository invoiceRepository;

    public GstReturnService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // =========================================
    // GSTR-1 (outward supplies, one row per invoice)
    // =========================================

    @Transactional(readOnly = true)
    public List<Gstr1Row> getGstr1(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        List<Gstr1Row> rows = new ArrayList<>();

        if (fromDate == null || toDate == null) {
            return rows;
        }

        List<Invoice> invoices =
                invoiceRepository.findInvoicesForGstReport(
                        fromDate,
                        toDate
                );

        if (invoices == null) {
            return rows;
        }

        for (Invoice invoice : invoices) {

            if (invoice == null) {
                continue;
            }

            BigDecimal taxable = nz(invoice.getTotalTaxable());
            BigDecimal cgst = nz(invoice.getTotalCGST());
            BigDecimal sgst = nz(invoice.getTotalSGST());
            BigDecimal igst = nz(invoice.getTotalIGST());
            BigDecimal total = nz(invoice.getGrandTotal());

            rows.add(new Gstr1Row(
                    invoice.getInvoiceNumber(),
                    invoice.getInvoiceDate(),
                    invoice.getCustomerName(),
                    invoice.getCustomerGSTNumber(),
                    invoice.getCustomerState(),
                    taxable,
                    cgst,
                    sgst,
                    igst,
                    total,
                    effectiveRate(invoice, taxable, cgst, sgst, igst)
            ));
        }

        return rows;
    }

    // =========================================
    // GSTR-3B (period summary)
    // =========================================

    @Transactional(readOnly = true)
    public Gstr3bSummary getGstr3b(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        Gstr3bSummary summary = new Gstr3bSummary();

        if (fromDate == null || toDate == null) {
            return summary;
        }

        List<Invoice> invoices =
                invoiceRepository.findInvoicesForGstReport(
                        fromDate,
                        toDate
                );

        if (invoices == null || invoices.isEmpty()) {
            return summary;
        }

        BigDecimal taxable = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;
        long count = 0;

        for (Invoice invoice : invoices) {

            if (invoice == null) {
                continue;
            }

            taxable = taxable.add(nz(invoice.getTotalTaxable()));
            cgst = cgst.add(nz(invoice.getTotalCGST()));
            sgst = sgst.add(nz(invoice.getTotalSGST()));
            igst = igst.add(nz(invoice.getTotalIGST()));
            count++;
        }

        summary.setTotalTaxableValue(taxable);
        summary.setTotalCgst(cgst);
        summary.setTotalSgst(sgst);
        summary.setTotalIgst(igst);
        summary.setTotalTax(cgst.add(sgst).add(igst));
        summary.setInvoiceCount(count);

        return summary;
    }

    // =========================================
    // HELPERS
    // =========================================

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * Effective tax rate (%) for the invoice. Prefer the line-item GST rate
     * when all lines share one rate; otherwise derive it from total tax over
     * taxable value. Returns zero when it cannot be determined.
     */
    private BigDecimal effectiveRate(
            Invoice invoice,
            BigDecimal taxable,
            BigDecimal cgst,
            BigDecimal sgst,
            BigDecimal igst
    ) {

        BigDecimal itemRate = uniformItemRate(invoice);
        if (itemRate != null) {
            return itemRate;
        }

        if (taxable == null || taxable.signum() == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = nz(cgst).add(nz(sgst)).add(nz(igst));

        return tax
                .multiply(BigDecimal.valueOf(100))
                .divide(taxable, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal uniformItemRate(Invoice invoice) {

        List<InvoiceItem> items = invoice.getItems();
        if (items == null || items.isEmpty()) {
            return null;
        }

        BigDecimal rate = null;

        for (InvoiceItem item : items) {

            if (item == null) {
                continue;
            }

            BigDecimal lineRate = nz(item.getGstRate());

            if (rate == null) {
                rate = lineRate;
            } else if (rate.compareTo(lineRate) != 0) {
                return null;
            }
        }

        return rate;
    }
}
