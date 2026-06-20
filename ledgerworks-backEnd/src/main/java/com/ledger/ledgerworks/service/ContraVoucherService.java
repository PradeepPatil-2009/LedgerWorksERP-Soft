package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ContraVoucher;
import com.ledger.ledgerworks.repository.ContraVoucherRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contra Voucher - cash &lt;-&gt; bank (asset to asset) transfer.
 * Ledger posting: debit the "to" account, credit the "from" account.
 *
 * Posting is delegated to {@link AccountingPostingService#postContra} so that
 * the {@link com.ledger.ledgerworks.entity.LedgerTransaction} rows are the ONE
 * source of truth - no direct LedgerAccount.balance mutation happens here.
 */
@Service
public class ContraVoucherService {

    private static final Logger log = LoggerFactory.getLogger(ContraVoucherService.class);

    private final ContraVoucherRepository repository;
    private final AccountingPostingService accountingPostingService;
    private final FinancialYearService financialYearService;
    private final DocumentNumberService documentNumberService;

    public ContraVoucherService(ContraVoucherRepository repository,
                                AccountingPostingService accountingPostingService,
                                FinancialYearService financialYearService,
                                DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.accountingPostingService = accountingPostingService;
        this.financialYearService = financialYearService;
        this.documentNumberService = documentNumberService;
    }

    public List<ContraVoucher> findAll() {
        return repository.findAllLatestFirst();
    }

    public ContraVoucher findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contra voucher not found"));
    }

    @Transactional
    public ContraVoucher save(ContraVoucher voucher) {

        if (voucher.getAmount() == null) {
            voucher.setAmount(BigDecimal.ZERO);
        }

        // Auto voucher number
        if (voucher.getVoucherNumber() == null || voucher.getVoucherNumber().isBlank()) {
            String last = repository.findTopByOrderByIdDesc()
                    .map(ContraVoucher::getVoucherNumber)
                    .orElse(null);
            voucher.setVoucherNumber(documentNumberService.generateNumber("CV", last));
        }

        ContraVoucher saved = repository.save(voucher);

        // Balanced ledger posting on the voucher's own date (best-effort).
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: Dr "to" account ; Cr "from" account
    // =====================================================
    private void postLedger(ContraVoucher voucher) {
        try {
            financialYearService.assertOpen(voucher.getDate());

            accountingPostingService.postContra(
                    voucher.getFromAccount(),
                    voucher.getToAccount(),
                    voucher.getAmount(),
                    voucher.getDate());
        } catch (Exception ex) {
            log.warn("Contra voucher ledger posting skipped: {}", ex.getMessage());
        }
    }
}
