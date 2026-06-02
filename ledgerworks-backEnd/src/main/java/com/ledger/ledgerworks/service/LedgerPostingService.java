package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerPostingService {

    private final LedgerTransactionService ledgerTransactionService;
    private final LedgerAccountRepository accountRepository;

    public LedgerPostingService(
            LedgerTransactionService ledgerTransactionService,
            LedgerAccountRepository accountRepository) {

        this.ledgerTransactionService = ledgerTransactionService;
        this.accountRepository = accountRepository;
    }

    // Invoice Posting
    @Transactional
    public void postInvoice(Invoice invoice) {

        LedgerAccount customer = accountRepository
                .findByAccountName(invoice.getCustomerName())
                .orElseThrow(() -> new RuntimeException("Customer account not found"));

        LedgerAccount sales = accountRepository
                .findByAccountName("Sales")
                .orElseThrow(() -> new RuntimeException("Sales account not found"));

        ledgerTransactionService.createTransaction(
                customer.getId(),
                sales.getId(),
                invoice.getGrandTotal(),
                "Invoice " + invoice.getInvoiceNumber()
        );
    }

    // Payment Posting
    @Transactional
    public void postInvoicePayment(
            Invoice invoice,
            Long bankAccountId,
            BigDecimal amount,
            String narration) {

        LedgerAccount customer = accountRepository
                .findByAccountName(invoice.getCustomerName())
                .orElseThrow(() -> new RuntimeException("Customer account not found"));

        ledgerTransactionService.createTransaction(
                bankAccountId,
                customer.getId(),
                amount,
                narration
        );
    }
}