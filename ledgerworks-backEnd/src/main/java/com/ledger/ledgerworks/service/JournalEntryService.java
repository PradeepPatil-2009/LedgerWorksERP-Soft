package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.JournalEntry;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.repository.JournalEntryRepository;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class JournalEntryService {

    private final JournalEntryRepository journalRepo;
    private final LedgerTransactionRepository ledgerRepo;
    private final LedgerAccountRepository accountRepo;

    public JournalEntryService(JournalEntryRepository journalRepo,
                               LedgerTransactionRepository ledgerRepo,
                               LedgerAccountRepository accountRepo) {
        this.journalRepo = journalRepo;
        this.ledgerRepo = ledgerRepo;
        this.accountRepo = accountRepo;
    }

    @Transactional
    public JournalEntry createJournalEntry(JournalEntry entry) {

        // ✅ Safety check
        if (entry.getDescription() == null) {
            entry.setDescription("");
        }

        if (entry.getAmount() == null) {
            throw new RuntimeException("Amount is required");
        }

        // ✅ Fetch accounts from DB (IMPORTANT)
        LedgerAccount debitAccount = accountRepo.findById(entry.getDebitAccount().getId())
                .orElseThrow(() -> new RuntimeException("Debit Account not found"));

        LedgerAccount creditAccount = accountRepo.findById(entry.getCreditAccount().getId())
                .orElseThrow(() -> new RuntimeException("Credit Account not found"));

        BigDecimal amount = entry.getAmount();

        // ================================
        // 💰 UPDATE BALANCE (CORE LOGIC)
        // ================================
        debitAccount.setBalance(debitAccount.getBalance().add(amount));   // + Debit
        creditAccount.setBalance(creditAccount.getBalance().subtract(amount)); // - Credit

        accountRepo.save(debitAccount);
        accountRepo.save(creditAccount);

        // ================================
        // 🧾 SAVE JOURNAL ENTRY
        // ================================
        entry.setDebitAccount(debitAccount);
        entry.setCreditAccount(creditAccount);

        JournalEntry saved = journalRepo.save(entry);

        // ================================
        // 📘 SAVE LEDGER TRANSACTION
        // ================================
        LedgerTransaction txn = new LedgerTransaction();
        txn.setTransactionDate(entry.getEntryDate());
        txn.setDebitAccount(debitAccount);
        txn.setCreditAccount(creditAccount);
        txn.setAmount(amount);
        txn.setDescription(entry.getDescription());

        ledgerRepo.save(txn);

        return saved;
    }
}