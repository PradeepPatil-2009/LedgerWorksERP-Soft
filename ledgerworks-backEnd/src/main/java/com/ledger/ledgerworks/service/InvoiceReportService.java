package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.InvoiceOutstandingRow;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceReportService {

    private final InvoiceRepository invoiceRepository;
    private final LedgerAccountRepository accountRepository;

    public InvoiceReportService(InvoiceRepository invoiceRepository,
                                LedgerAccountRepository accountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
    }

    public List<InvoiceOutstandingRow> getInvoiceWiseOutstanding() {

        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceOutstandingRow> report = new ArrayList<>();

        for (Invoice invoice : invoices) {

            LedgerAccount customerAccount =
                    accountRepository.findByAccountName(invoice.getCustomerName())
                            .orElse(null);

            BigDecimal paidAmount = BigDecimal.ZERO;

            if (customerAccount != null) {
                BigDecimal currentBalance = customerAccount.getBalance();

                if (currentBalance.compareTo(BigDecimal.ZERO) > 0) {
                    paidAmount = invoice.getGrandTotal().subtract(currentBalance);
                } else {
                    paidAmount = invoice.getGrandTotal();
                }
            }

            BigDecimal balanceAmount =
                    invoice.getGrandTotal().subtract(paidAmount);

            String status;

            if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
                status = "PAID";
            } else if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                status = "UNPAID";
            } else {
                status = "PARTIAL";
            }

            report.add(new InvoiceOutstandingRow(
                    invoice.getInvoiceNumber(),
                    invoice.getInvoiceDate(),
                    invoice.getCustomerName(),
                    invoice.getGrandTotal(),
                    paidAmount,
                    balanceAmount,
                    status
            ));
        }

        return report;
    }
}