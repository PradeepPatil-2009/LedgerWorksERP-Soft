package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.DebitNote;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.DebitNoteRepository;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DebitNoteService {

    private static final Logger log =
            LoggerFactory.getLogger(DebitNoteService.class);

    private final DebitNoteRepository repository;
    private final LedgerAccountRepository accountRepository;
    private final DocumentNumberService documentNumberService;

    public DebitNoteService(
            DebitNoteRepository repository,
            LedgerAccountRepository accountRepository,
            DocumentNumberService documentNumberService) {

        this.repository = repository;
        this.accountRepository = accountRepository;
        this.documentNumberService = documentNumberService;
    }

    // ================= GET ALL =================

    public List<DebitNote> getAll() {
        return repository.findAll();
    }

    // ================= GET BY ID =================

    public DebitNote getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Debit note not found"));
    }

    // ================= CREATE =================

    public DebitNote create(DebitNote note) {

        if (note.getAmount() == null) {
            note.setAmount(BigDecimal.ZERO);
        }

        if (note.getGstAmount() == null) {
            note.setGstAmount(BigDecimal.ZERO);
        }

        if (note.getDate() == null) {
            note.setDate(LocalDate.now());
        }

        // ================= DOCUMENT NUMBER =================

        if (note.getNoteNumber() == null
                || note.getNoteNumber().isBlank()) {

            DebitNote last = repository.findTopByOrderByIdDesc();

            String lastNumber =
                    last != null ? last.getNoteNumber() : null;

            note.setNoteNumber(
                    documentNumberService.generateNumber("DN", lastNumber));
        }

        DebitNote saved = repository.save(note);

        // ================= LEDGER (best-effort) =================

        postLedger(saved);

        return saved;
    }

    // =====================================================
    // Reduce vendor outstanding (payable) by the note value.
    // Best-effort: never block the save if posting fails.
    // =====================================================

    private void postLedger(DebitNote note) {

        try {

            BigDecimal total = note.getAmount()
                    .add(note.getGstAmount());

            if (note.getPartyName() == null
                    || note.getPartyName().isBlank()) {
                return;
            }

            LedgerAccount vendor = accountRepository
                    .findByAccountName(note.getPartyName())
                    .orElse(null);

            if (vendor == null) {
                log.info(
                        "Debit note {}: no ledger account for '{}', skipping posting",
                        note.getNoteNumber(), note.getPartyName());
                return;
            }

            BigDecimal balance = vendor.getBalance() != null
                    ? vendor.getBalance()
                    : BigDecimal.ZERO;

            vendor.setBalance(balance.subtract(total));

            accountRepository.save(vendor);

        } catch (Exception ex) {
            log.warn(
                    "Debit note ledger posting failed for {}: {}",
                    note.getNoteNumber(), ex.getMessage());
        }
    }
}
