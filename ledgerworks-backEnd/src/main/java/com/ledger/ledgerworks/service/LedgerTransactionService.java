package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LedgerTransactionService {

    private final LedgerTransactionRepository transactionRepository;
    private final LedgerAccountRepository accountRepository;

    public LedgerTransactionService(LedgerTransactionRepository transactionRepository,
                                    LedgerAccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    // =====================================================
    // ✅ GENERIC CREATE TRANSACTION (Used by Posting Layer)
    //    Legacy signature - stamps the transaction with today's date.
    //    Delegates to the dated overload with now().
    // =====================================================
    public void createTransaction(
            Long debitAccountId,
            Long creditAccountId,
            BigDecimal amount,
            String narration) {

        createTransaction(
                debitAccountId,
                creditAccountId,
                amount,
                LocalDate.now(),
                narration);
    }

    // =====================================================
    // ✅ DATED CREATE TRANSACTION (Document Posting Layer)
    //    Stamps the transaction with the DOCUMENT's own date so the
    //    ledger / reports reflect the document date, not the import time.
    //    Falls back to today when date is null.
    // =====================================================
    public void createTransaction(
            Long debitAccountId,
            Long creditAccountId,
            BigDecimal amount,
            LocalDate date,
            String narration) {

        LedgerAccount debitAccount = accountRepository.findById(debitAccountId)
                .orElseThrow(() -> new RuntimeException("Debit account not found"));

        LedgerAccount creditAccount = accountRepository.findById(creditAccountId)
                .orElseThrow(() -> new RuntimeException("Credit account not found"));

        LedgerTransaction transaction = new LedgerTransaction();
        transaction.setDebitAccount(debitAccount);
        transaction.setCreditAccount(creditAccount);
        transaction.setAmount(amount);
        transaction.setNarration(narration);
        transaction.setTransactionDate(date != null ? date : LocalDate.now());

        transactionRepository.save(transaction);
    }

    // =====================================================
    // OPTIONAL DIRECT POST (If Needed)
    // =====================================================
    public LedgerTransaction postTransaction(LedgerTransaction transaction) {

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDate.now());
        }

        return transactionRepository.save(transaction);
    }

    // =====================================================
    // REVERSE
    // =====================================================
    public void reverseTransaction(Long id) {

        LedgerTransaction original = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        LedgerTransaction reverse = new LedgerTransaction();
        reverse.setDebitAccount(original.getCreditAccount());
        reverse.setCreditAccount(original.getDebitAccount());
        reverse.setAmount(original.getAmount());
        reverse.setNarration("Reversal of TXN ID: " + id);
        reverse.setTransactionDate(LocalDate.now());

        transactionRepository.save(reverse);
    }
}