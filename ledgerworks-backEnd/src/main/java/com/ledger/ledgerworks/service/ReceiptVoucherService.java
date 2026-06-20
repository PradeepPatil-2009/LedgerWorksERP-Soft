package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.ReceiptVoucher;
import com.ledger.ledgerworks.enums.VoucherPaymentMode;
import com.ledger.ledgerworks.repository.ReceiptVoucherRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Receipt Voucher - money received FROM a customer.
 * Ledger posting: debit Cash/Bank, credit the customer (Debtors).
 *
 * Posting is delegated to {@link AccountingPostingService#postReceipt} so that
 * the {@link com.ledger.ledgerworks.entity.LedgerTransaction} rows are the ONE
 * source of truth - no direct LedgerAccount.balance mutation happens here.
 */
@Service
public class ReceiptVoucherService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptVoucherService.class);

    private final ReceiptVoucherRepository repository;
    private final AccountingPostingService accountingPostingService;
    private final FinancialYearService financialYearService;
    private final DocumentNumberService documentNumberService;

    public ReceiptVoucherService(ReceiptVoucherRepository repository,
                                 AccountingPostingService accountingPostingService,
                                 FinancialYearService financialYearService,
                                 DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.accountingPostingService = accountingPostingService;
        this.financialYearService = financialYearService;
        this.documentNumberService = documentNumberService;
    }

    public List<ReceiptVoucher> findAll() {
        return repository.findAllLatestFirst();
    }

    public ReceiptVoucher findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt voucher not found"));
    }

    @Transactional
    public ReceiptVoucher save(ReceiptVoucher voucher) {

        if (voucher.getAmount() == null) {
            voucher.setAmount(BigDecimal.ZERO);
        }

        // Auto voucher number
        if (voucher.getVoucherNumber() == null || voucher.getVoucherNumber().isBlank()) {
            String last = repository.findTopByOrderByIdDesc()
                    .map(ReceiptVoucher::getVoucherNumber)
                    .orElse(null);
            voucher.setVoucherNumber(documentNumberService.generateNumber("RV", last));
        }

        ReceiptVoucher saved = repository.save(voucher);

        // Balanced ledger posting on the voucher's own date (best-effort).
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: Dr Cash/Bank ; Cr customer (Debtors)
    // =====================================================
    private void postLedger(ReceiptVoucher voucher) {
        try {
            financialYearService.assertOpen(voucher.getDate());

            String partyName = (voucher.getPartyName() != null && !voucher.getPartyName().isBlank())
                    ? voucher.getPartyName()
                    : "Sundry Debtors";

            VoucherPaymentMode mode = voucher.getPaymentMode() != null
                    ? voucher.getPaymentMode()
                    : VoucherPaymentMode.CASH;

            accountingPostingService.postReceipt(
                    partyName, voucher.getAmount(), mode.name(), voucher.getDate());
        } catch (Exception ex) {
            log.warn("Receipt voucher ledger posting skipped: {}", ex.getMessage());
        }
    }
}
