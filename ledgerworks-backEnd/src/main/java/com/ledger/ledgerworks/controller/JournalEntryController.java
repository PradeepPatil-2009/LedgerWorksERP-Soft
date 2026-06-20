package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.JournalRowRequest;
import com.ledger.ledgerworks.entity.JournalEntry;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.service.JournalEntryService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journal")
public class JournalEntryController {

    private final JournalEntryService service;
    private final LedgerAccountRepository accountRepository;

    public JournalEntryController(JournalEntryService service,
                                  LedgerAccountRepository accountRepository) {
        this.service = service;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public String create(@RequestBody List<JournalRowRequest> rows) {

        LedgerAccount debitAccount = null;
        LedgerAccount creditAccount = null;
        BigDecimal amount = BigDecimal.ZERO;

        for (JournalRowRequest row : rows) {

            if (row.getAccountId() == null) {
                throw new RuntimeException("Account is required");
            }

            LedgerAccount acc = accountRepository.findById(row.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if ("DEBIT".equalsIgnoreCase(row.getType())) {
                debitAccount = acc;
                amount = row.getAmount();
            } else if ("CREDIT".equalsIgnoreCase(row.getType())) {
                creditAccount = acc;
            }
        }

        if (debitAccount == null || creditAccount == null) {
            throw new RuntimeException("Both Debit and Credit required");
        }

        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(LocalDate.now());
        entry.setDebitAccount(debitAccount);
        entry.setCreditAccount(creditAccount);
        entry.setAmount(amount);

        // ✅ FIX: description default
        entry.setDescription("Manual Entry");

        service.createJournalEntry(entry);

        return "Saved Successfully";
    }
}