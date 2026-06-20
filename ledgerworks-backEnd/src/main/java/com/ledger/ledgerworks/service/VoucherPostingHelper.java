package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.enums.VoucherPaymentMode;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Best-effort helper that resolves / auto-creates the ledger accounts that the
 * voucher posting needs and exposes a couple of safe convenience methods.
 *
 * Reuses the existing {@link LedgerAccountRepository} by injection. It never
 * throws on minor mapping gaps - on failure the caller simply skips posting and
 * still saves the voucher.
 */
@Service
public class VoucherPostingHelper {

    private static final Logger log = LoggerFactory.getLogger(VoucherPostingHelper.class);

    private final LedgerAccountRepository accountRepository;
    private final LedgerTransactionService ledgerTransactionService;

    public VoucherPostingHelper(LedgerAccountRepository accountRepository,
                                LedgerTransactionService ledgerTransactionService) {
        this.accountRepository = accountRepository;
        this.ledgerTransactionService = ledgerTransactionService;
    }

    // =====================================================
    // RESOLVE (OR CREATE) AN ACCOUNT BY NAME
    // =====================================================
    public LedgerAccount resolveAccount(String name, AccountType type) {

        if (name == null || name.isBlank()) {
            return null;
        }

        final String accountName = name.trim();

        return accountRepository.findByAccountName(accountName)
                .orElseGet(() -> {
                    LedgerAccount account = new LedgerAccount();
                    account.setAccountName(accountName);
                    account.setAccountType(type);
                    account.setBalance(BigDecimal.ZERO);
                    return accountRepository.save(account);
                });
    }

    // =====================================================
    // CASH / BANK ACCOUNT FOR A PAYMENT MODE
    // CASH -> "Cash";  BANK -> "Bank";  UPI -> "Bank"
    // =====================================================
    public LedgerAccount resolveCashOrBank(VoucherPaymentMode mode) {

        String name = (mode == VoucherPaymentMode.CASH) ? "Cash" : "Bank";
        return resolveAccount(name, AccountType.ASSET);
    }

    // =====================================================
    // SAFE POSTING - DEBIT / CREDIT BY ACCOUNT, BEST EFFORT
    // Returns true when the ledger entry was written.
    // =====================================================
    public boolean post(LedgerAccount debit,
                        LedgerAccount credit,
                        BigDecimal amount,
                        String narration) {

        if (debit == null || credit == null
                || amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Skipping voucher ledger posting - missing account or amount");
            return false;
        }

        try {
            ledgerTransactionService.createTransaction(
                    debit.getId(),
                    credit.getId(),
                    amount,
                    narration
            );
            return true;
        } catch (Exception ex) {
            log.warn("Voucher ledger posting failed (saved anyway): {}", ex.getMessage());
            return false;
        }
    }
}
