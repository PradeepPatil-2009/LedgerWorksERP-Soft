package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ContraVoucher;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;
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
 */
@Service
public class ContraVoucherService {

    private static final Logger log = LoggerFactory.getLogger(ContraVoucherService.class);

    private final ContraVoucherRepository repository;
    private final VoucherPostingHelper postingHelper;
    private final DocumentNumberService documentNumberService;

    public ContraVoucherService(ContraVoucherRepository repository,
                                VoucherPostingHelper postingHelper,
                                DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.postingHelper = postingHelper;
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

        // Best-effort ledger posting (never blocks the save)
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: debit "to" account, credit "from" account
    // =====================================================
    private void postLedger(ContraVoucher voucher) {
        try {
            LedgerAccount from = postingHelper.resolveAccount(voucher.getFromAccount(), AccountType.ASSET);
            LedgerAccount to = postingHelper.resolveAccount(voucher.getToAccount(), AccountType.ASSET);

            String narration = (voucher.getNarration() != null && !voucher.getNarration().isBlank())
                    ? voucher.getNarration()
                    : "Contra " + voucher.getVoucherNumber();

            postingHelper.post(to, from, voucher.getAmount(), narration);
        } catch (Exception ex) {
            log.warn("Contra voucher ledger posting skipped: {}", ex.getMessage());
        }
    }
}
