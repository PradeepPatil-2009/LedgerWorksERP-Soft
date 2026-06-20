package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.DebitNote;
import com.ledger.ledgerworks.repository.DebitNoteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DebitNoteService {

    private static final Logger log =
            LoggerFactory.getLogger(DebitNoteService.class);

    private final DebitNoteRepository repository;
    private final DocumentNumberService documentNumberService;
    private final AccountingPostingService accountingPostingService;
    private final FinancialYearService financialYearService;

    public DebitNoteService(
            DebitNoteRepository repository,
            DocumentNumberService documentNumberService,
            AccountingPostingService accountingPostingService,
            FinancialYearService financialYearService) {

        this.repository = repository;
        this.documentNumberService = documentNumberService;
        this.accountingPostingService = accountingPostingService;
        this.financialYearService = financialYearService;
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

    @Transactional
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

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(note.getDate());

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
    // Purchase return: reverse the purchase on the note's own date
    //   Dr vendor-creditor ; Cr Purchases (taxable) + Cr Input CGST/SGST/IGST
    // Best-effort: never block the save if posting fails.
    // =====================================================

    private void postLedger(DebitNote note) {

        try {

            BigDecimal taxable = taxableValue(note);
            BigDecimal cgst = nz(note.getCgstAmount());
            BigDecimal sgst = nz(note.getSgstAmount());
            BigDecimal igst = nz(note.getIgstAmount());
            BigDecimal grandTotal = nz(note.getAmount()).add(nz(note.getGstAmount()));

            accountingPostingService.postPurchaseReturn(
                    note.getPartyName(),
                    taxable, cgst, sgst, igst,
                    grandTotal,
                    note.getDate(),
                    note.getNoteNumber());

        } catch (Exception ex) {
            log.warn(
                    "Debit note ledger posting failed for {}: {}",
                    note.getNoteNumber(), ex.getMessage());
        }
    }

    // Prefer the explicit taxable column; fall back to the legacy amount when
    // only the older (amount + gstAmount) fields were supplied.
    private static BigDecimal taxableValue(DebitNote note) {
        BigDecimal taxable = nz(note.getTaxableValue());
        if (taxable.signum() != 0) {
            return taxable;
        }
        return nz(note.getAmount());
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
