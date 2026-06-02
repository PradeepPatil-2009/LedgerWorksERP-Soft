package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.DashboardResponse;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final InvoiceRepository invoiceRepository;

    public DashboardService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardResponse getDashboard() {

        List<Invoice> invoices = invoiceRepository.findAll();

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        BigDecimal overdueAmount = BigDecimal.ZERO;
        int overdueCount = 0;

        LocalDate today = LocalDate.now();

        for (Invoice invoice : invoices) {

            BigDecimal grandTotal = safe(invoice.getGrandTotal());
            BigDecimal outstanding = safe(invoice.getOutstandingAmount());

            totalSales = totalSales.add(grandTotal);
            totalOutstanding = totalOutstanding.add(outstanding);

            // ✅ Use dueDate (IMPORTANT FIX)
            if (invoice.getDueDate() != null &&
                invoice.getDueDate().isBefore(today) &&
                outstanding.compareTo(BigDecimal.ZERO) > 0) {

                overdueAmount = overdueAmount.add(outstanding);
                overdueCount++;
            }
        }

        return new DashboardResponse(
                totalSales,
                totalOutstanding,
                overdueAmount,
                overdueCount
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}