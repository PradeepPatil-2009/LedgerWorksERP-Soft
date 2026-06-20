package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.CreditNote;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.CreditNoteRepository;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreditNoteService {

    private static final Logger log =
            LoggerFactory.getLogger(CreditNoteService.class);

    private final CreditNoteRepository repository;
    private final DocumentNumberService documentNumberService;
    private final AccountingPostingService accountingPostingService;
    private final FinancialYearService financialYearService;
    private final InvoiceRepository invoiceRepository;

    public CreditNoteService(
            CreditNoteRepository repository,
            DocumentNumberService documentNumberService,
            AccountingPostingService accountingPostingService,
            FinancialYearService financialYearService,
            InvoiceRepository invoiceRepository) {

        this.repository = repository;
        this.documentNumberService = documentNumberService;
        this.accountingPostingService = accountingPostingService;
        this.financialYearService = financialYearService;
        this.invoiceRepository = invoiceRepository;
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

    @Transactional
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

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(note.getDate());

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

        // ================= LEDGER + RECEIVABLE (best-effort) =================

        postLedger(saved);

        return saved;
    }

    // =====================================================
    // Sales return: reverse the sale on the note's own date
    //   Dr Sales (taxable) + Dr Output CGST/SGST/IGST ; Cr customer-debtor
    // and reduce the referenced invoice's receivable so aging stays correct.
    // Best-effort: never block the save if posting fails.
    // =====================================================

    private void postLedger(CreditNote note) {

        try {

            BigDecimal taxable = taxableValue(note);
            BigDecimal cgst = nz(note.getCgstAmount());
            BigDecimal sgst = nz(note.getSgstAmount());
            BigDecimal igst = nz(note.getIgstAmount());
            BigDecimal grandTotal = nz(note.getAmount()).add(nz(note.getGstAmount()));

            accountingPostingService.postSalesReturn(
                    note.getPartyName(),
                    taxable, cgst, sgst, igst,
                    grandTotal,
                    note.getDate(),
                    note.getNoteNumber());

            // Reduce the referenced invoice's receivable (outstanding / paid)
            // and recompute the payment status, when it resolves.
            applyToReferencedInvoice(note, grandTotal);

        } catch (Exception ex) {
            log.warn(
                    "Credit note ledger posting failed for {}: {}",
                    note.getNoteNumber(), ex.getMessage());
        }
    }

    // Reduce the referenced invoice's outstanding by the note total. A sales
    // return settles part of the receivable, so we treat the note total like a
    // credit against the invoice: bump paidAmount, recompute outstanding (kept
    // as grandTotal - paidAmount, the invariant the outstanding/aging queries
    // rely on) and the payment status.
    private void applyToReferencedInvoice(CreditNote note, BigDecimal noteTotal) {

        if (note.getInvoiceReference() == null
                || note.getInvoiceReference().isBlank()) {
            return;
        }

        Invoice invoice = invoiceRepository
                .findByInvoiceNumber(note.getInvoiceReference().trim())
                .orElse(null);

        if (invoice == null) {
            log.info("Credit note {}: no invoice for reference '{}', "
                            + "skipping receivable adjustment",
                    note.getNoteNumber(), note.getInvoiceReference());
            return;
        }

        BigDecimal grandTotal = nz(invoice.getGrandTotal());
        BigDecimal newPaid = nz(invoice.getPaidAmount()).add(nz(noteTotal));

        // Never let credits exceed the invoice value.
        if (newPaid.compareTo(grandTotal) > 0) {
            newPaid = grandTotal;
        }

        BigDecimal outstanding = grandTotal.subtract(newPaid);

        invoice.setPaidAmount(newPaid);
        invoice.setOutstandingAmount(outstanding);

        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setPaymentStatus("PAID");
            invoice.setPaidStatus("PAID");
        } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setPaymentStatus("PARTIAL");
            invoice.setPaidStatus("PARTIAL");
        } else {
            invoice.setPaymentStatus("UNPAID");
            invoice.setPaidStatus("UNPAID");
        }

        invoiceRepository.save(invoice);
    }

    // Prefer the explicit taxable column; fall back to the legacy amount when
    // only the older (amount + gstAmount) fields were supplied.
    private static BigDecimal taxableValue(CreditNote note) {
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
