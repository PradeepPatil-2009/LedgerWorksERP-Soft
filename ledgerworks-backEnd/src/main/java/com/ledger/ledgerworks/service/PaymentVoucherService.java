package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.PaymentVoucher;
import com.ledger.ledgerworks.enums.AccountType;
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
 */
@Service
public class PaymentVoucherService {

    private static final Logger log = LoggerFactory.getLogger(PaymentVoucherService.class);

    private final PaymentVoucherRepository repository;
    private final VoucherPostingHelper postingHelper;
    private final DocumentNumberService documentNumberService;

    public PaymentVoucherService(PaymentVoucherRepository repository,
                                 VoucherPostingHelper postingHelper,
                                 DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.postingHelper = postingHelper;
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

        // Best-effort ledger posting (never blocks the save)
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: debit vendor (Creditors), credit Cash/Bank
    // =====================================================
    private void postLedger(PaymentVoucher voucher) {
        try {
            LedgerAccount cashOrBank = postingHelper.resolveCashOrBank(voucher.getPaymentMode());

            String partyName = (voucher.getPartyName() != null && !voucher.getPartyName().isBlank())
                    ? voucher.getPartyName()
                    : "Sundry Creditors";

            LedgerAccount vendor = postingHelper.resolveAccount(partyName, AccountType.LIABILITY);

            String narration = (voucher.getNarration() != null && !voucher.getNarration().isBlank())
                    ? voucher.getNarration()
                    : "Payment " + voucher.getVoucherNumber();

            boolean posted = postingHelper.post(vendor, cashOrBank, voucher.getAmount(), narration);

            if (posted) {
                reduceOutstanding(vendor, voucher.getAmount());
            }
        } catch (Exception ex) {
            log.warn("Payment voucher ledger posting skipped: {}", ex.getMessage());
        }
    }

    // Reduce the vendor's outstanding (liability balance) by the paid amount.
    private void reduceOutstanding(LedgerAccount vendor, BigDecimal amount) {
        try {
            if (vendor != null && amount != null) {
                BigDecimal current = vendor.getBalance() != null ? vendor.getBalance() : BigDecimal.ZERO;
                vendor.setBalance(current.subtract(amount));
            }
        } catch (Exception ex) {
            log.warn("Could not adjust vendor outstanding: {}", ex.getMessage());
        }
    }
}
