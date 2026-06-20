package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.PaymentVoucher;
import com.ledger.ledgerworks.enums.VoucherPaymentMode;
import com.ledger.ledgerworks.repository.PaymentVoucherRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payment Voucher - money paid TO a vendor.
 * Ledger posting: debit the vendor (Creditors), credit Cash/Bank.
 *
 * Posting is delegated to {@link AccountingPostingService#postPayment} so that
 * the {@link com.ledger.ledgerworks.entity.LedgerTransaction} rows are the ONE
 * source of truth - no direct LedgerAccount.balance mutation happens here.
 */
@Service
public class PaymentVoucherService {

    private static final Logger log = LoggerFactory.getLogger(PaymentVoucherService.class);

    private final PaymentVoucherRepository repository;
    private final AccountingPostingService accountingPostingService;
    private final FinancialYearService financialYearService;
    private final DocumentNumberService documentNumberService;

    public PaymentVoucherService(PaymentVoucherRepository repository,
                                 AccountingPostingService accountingPostingService,
                                 FinancialYearService financialYearService,
                                 DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.accountingPostingService = accountingPostingService;
        this.financialYearService = financialYearService;
        this.documentNumberService = documentNumberService;
    }

    public List<PaymentVoucher> findAll() {
        return repository.findAllLatestFirst();
    }

    public PaymentVoucher findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment voucher not found"));
    }

    @Transactional
    public PaymentVoucher save(PaymentVoucher voucher) {

        if (voucher.getAmount() == null) {
            voucher.setAmount(BigDecimal.ZERO);
        }

        // Auto voucher number
        if (voucher.getVoucherNumber() == null || voucher.getVoucherNumber().isBlank()) {
            String last = repository.findTopByOrderByIdDesc()
                    .map(PaymentVoucher::getVoucherNumber)
                    .orElse(null);
            voucher.setVoucherNumber(documentNumberService.generateNumber("PV", last));
        }

        PaymentVoucher saved = repository.save(voucher);

        // Balanced ledger posting on the voucher's own date (best-effort).
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: Dr vendor (Creditors) ; Cr Cash/Bank
    // =====================================================
    private void postLedger(PaymentVoucher voucher) {
        try {
            financialYearService.assertOpen(voucher.getDate());

            String partyName = (voucher.getPartyName() != null && !voucher.getPartyName().isBlank())
                    ? voucher.getPartyName()
                    : "Sundry Creditors";

            VoucherPaymentMode mode = voucher.getPaymentMode() != null
                    ? voucher.getPaymentMode()
                    : VoucherPaymentMode.CASH;

            accountingPostingService.postPayment(
                    partyName, voucher.getAmount(), mode.name(), voucher.getDate());
        } catch (Exception ex) {
            log.warn("Payment voucher ledger posting skipped: {}", ex.getMessage());
        }
    }
}
