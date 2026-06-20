package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class LedgerAccountController {

    private final LedgerAccountRepository accountRepository;

    public LedgerAccountController(LedgerAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ✅ GET ALL
    @GetMapping
    public List<LedgerAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    // ✅ CREATE ACCOUNT (IMPORTANT)
    @PostMapping
    public LedgerAccount createAccount(@RequestBody LedgerAccount account) {

        if (account.getBalance() == null) {
            account.setBalance(java.math.BigDecimal.ZERO);
        }

        return accountRepository.save(account);
    }
}