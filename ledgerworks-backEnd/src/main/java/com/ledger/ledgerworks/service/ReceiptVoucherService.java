package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.ReceiptVoucher;
import com.ledger.ledgerworks.enums.AccountType;
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
 */
@Service
public class ReceiptVoucherService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptVoucherService.class);

    private final ReceiptVoucherRepository repository;
    private final VoucherPostingHelper postingHelper;
    private final DocumentNumberService documentNumberService;

    public ReceiptVoucherService(ReceiptVoucherRepository repository,
                                 VoucherPostingHelper postingHelper,
                                 DocumentNumberService documentNumberService) {
        this.repository = repository;
        this.postingHelper = postingHelper;
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

        // Best-effort ledger posting (never blocks the save)
        postLedger(saved);

        return saved;
    }

    // =====================================================
    // LEDGER: debit Cash/Bank, credit customer (Debtors)
    // =====================================================
    private void postLedger(ReceiptVoucher voucher) {
        try {
            LedgerAccount cashOrBank = postingHelper.resolveCashOrBank(voucher.getPaymentMode());

            String partyName = (voucher.getPartyName() != null && !voucher.getPartyName().isBlank())
                    ? voucher.getPartyName()
                    : "Sundry Debtors";

            LedgerAccount customer = postingHelper.resolveAccount(partyName, AccountType.ASSET);

            String narration = (voucher.getNarration() != null && !voucher.getNarration().isBlank())
                    ? voucher.getNarration()
                    : "Receipt " + voucher.getVoucherNumber();

            boolean posted = postingHelper.post(cashOrBank, customer, voucher.getAmount(), narration);

            if (posted) {
                reduceOutstanding(customer, voucher.getAmount());
            }
        } catch (Exception ex) {
            log.warn("Receipt voucher ledger posting skipped: {}", ex.getMessage());
        }
    }

    // Reduce the customer's outstanding (asset balance) by the received amount.
    private void reduceOutstanding(LedgerAccount customer, BigDecimal amount) {
        try {
            if (customer != null && amount != null) {
                BigDecimal current = customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO;
                customer.setBalance(current.subtract(amount));
            }
        } catch (Exception ex) {
            log.warn("Could not adjust customer outstanding: {}", ex.getMessage());
        }
    }
}
