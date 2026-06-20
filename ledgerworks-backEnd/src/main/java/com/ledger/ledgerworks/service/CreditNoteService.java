package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CreditNote;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.CreditNoteRepository;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreditNoteService {

    private static final Logger log =
            LoggerFactory.getLogger(CreditNoteService.class);

    private final CreditNoteRepository repository;
    private final LedgerAccountRepository accountRepository;
    private final DocumentNumberService documentNumberService;

    public CreditNoteService(
            CreditNoteRepository repository,
            LedgerAccountRepository accountRepository,
            DocumentNumberService documentNumberService) {

        this.repository = repository;
        this.accountRepository = accountRepository;
        this.documentNumberService = documentNumberService;
    }

    // ================= GET ALL =================

    public List<CreditNote> getAll() {
        return repository.findAll();
    }

    // ================= GET BY ID =================

    public CreditNote getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Credit note not found"));
    }

    // ================= CREATE =================

    public CreditNote create(CreditNote note) {

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

            CreditNote last = repository.findTopByOrderByIdDesc();

            String lastNumber =
                    last != null ? last.getNoteNumber() : null;

            note.setNoteNumber(
                    documentNumberService.generateNumber("CN", lastNumber));
        }

        CreditNote saved = repository.save(note);

        // ================= LEDGER (best-effort) =================

        postLedger(saved);

        return saved;
    }

    // =====================================================
    // Reduce customer outstanding by the total note value.
    // Best-effort: never block the save if posting fails.
    // =====================================================

    private void postLedger(CreditNote note) {

        try {

            BigDecimal total = note.getAmount()
                    .add(note.getGstAmount());

            if (note.getPartyName() == null
                    || note.getPartyName().isBlank()) {
                return;
            }

            LedgerAccount customer = accountRepository
                    .findByAccountName(note.getPartyName())
                    .orElse(null);

            if (customer == null) {
                log.info(
                        "Credit note {}: no ledger account for '{}', skipping posting",
                        note.getNoteNumber(), note.getPartyName());
                return;
            }

            BigDecimal balance = customer.getBalance() != null
                    ? customer.getBalance()
                    : BigDecimal.ZERO;

            customer.setBalance(balance.subtract(total));

            accountRepository.save(customer);

        } catch (Exception ex) {
            log.warn(
                    "Credit note ledger posting failed for {}: {}",
                    note.getNoteNumber(), ex.getMessage());
        }
    }
}
