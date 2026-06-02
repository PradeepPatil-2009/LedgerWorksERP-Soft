/*package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LedgerAccountService {

    private static final Logger log = LoggerFactory.getLogger(LedgerAccountService.class);

    private final LedgerAccountRepository repository;

    public LedgerAccountService(LedgerAccountRepository repository) {
        this.repository = repository;
    }

    // =============================
    // SAVE ACCOUNT
    // =============================
    public LedgerAccount saveAccount(LedgerAccount account) {

        log.info("🔥 SAVE ACCOUNT API HIT: {}", account.getAccountName());

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        return repository.save(account);
    }

    // =============================
    // GET ALL ACCOUNTS
    // =============================
    public List<LedgerAccount> getAllAccounts() {

        log.info("📥 FETCH ALL ACCOUNTS API HIT");

        return repository.findAll();
    }

    // =============================
    // GET OR CREATE
    // =============================
    public LedgerAccount getOrCreate(String name, AccountType type) {

        log.info("🔍 GET OR CREATE ACCOUNT: {}", name);

        Optional<LedgerAccount> existing = repository.findByAccountName(name);

        if (existing.isPresent()) {
            return existing.get();
        }

        LedgerAccount account = new LedgerAccount();
        account.setAccountName(name);
        account.setAccountType(type);
        account.setBalance(BigDecimal.ZERO);

        return repository.save(account);
    }

    // =============================
    // DELETE ACCOUNT (SAFE DELETE)
    // =============================
    public void deleteAccount(Long id) {

        log.info("🗑 DELETE ACCOUNT API HIT - ID: {}", id);

        LedgerAccount account = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        try {
            repository.delete(account);

            log.info("✅ Account deleted successfully: {}", account.getAccountName());

        } catch (Exception e) {

            log.error("❌ Delete failed for account: {}", account.getAccountName(), e);

            throw new RuntimeException(
                    "Cannot delete account. It is used in transactions or invoices."
            );
        }
    }

    // =============================
    // UPDATE ACCOUNT
    // =============================
    public LedgerAccount updateAccount(Long id, LedgerAccount account) {

        log.info("✏️ UPDATE ACCOUNT API HIT - ID: {}", id);

        LedgerAccount existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        existing.setAccountName(account.getAccountName());
        existing.setAccountType(account.getAccountType());

        if (account.getBalance() != null) {
            existing.setBalance(account.getBalance());
        }

        return repository.save(existing);
    }
}

*/

package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LedgerAccountService {

    private final LedgerAccountRepository repository;

    public LedgerAccountService(LedgerAccountRepository repository) {
        this.repository = repository;
    }

    // =============================
    // SAVE ACCOUNT
    // =============================
    public LedgerAccount saveAccount(LedgerAccount account) {

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        return repository.save(account);
    }

    // =============================
    // GET ALL ACCOUNTS
    // =============================
    public List<LedgerAccount> getAllAccounts() {
        return repository.findAll();
    }

    // =============================
    // GET OR CREATE
    // =============================
    public LedgerAccount getOrCreate(String name, AccountType type) {

        Optional<LedgerAccount> existing = repository.findByAccountName(name);

        if (existing.isPresent()) {
            return existing.get();
        }

        LedgerAccount account = new LedgerAccount();
        account.setAccountName(name);
        account.setAccountType(type);
        account.setBalance(BigDecimal.ZERO);

        return repository.save(account);
    }

    // =============================
    // DELETE ACCOUNT
    // =============================
    public void deleteAccount(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Account not found with id: " + id);
        }

        repository.deleteById(id);
    }

    // =============================
    // UPDATE ACCOUNT
    // =============================
    public LedgerAccount updateAccount(Long id, LedgerAccount account) {

        LedgerAccount existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        existing.setAccountName(account.getAccountName());
        existing.setAccountType(account.getAccountType());

        if (account.getBalance() != null) {
            existing.setBalance(account.getBalance());
        }

        return repository.save(existing);
    }
}