package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.LedgerStatementRow;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.LedgerTransaction;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LedgerStatementService {

private final LedgerAccountRepository accountRepository;
private final LedgerTransactionRepository transactionRepository;

public LedgerStatementService(
        LedgerAccountRepository accountRepository,
        LedgerTransactionRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
}

public List<LedgerStatementRow> getStatement(
        Long accountId,
        LocalDate fromDate,
        LocalDate toDate) {

    LedgerAccount account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    // ✅ STEP 1: Opening Balance Calculation
    BigDecimal openingBalance = BigDecimal.ZERO;

    if (fromDate != null) {
        List<LedgerTransaction> previousTransactions =
                transactionRepository.findByAccountAndDateBetween(
                        accountId, null, fromDate.minusDays(1));

        for (LedgerTransaction tx : previousTransactions) {

            if (tx.getDebitAccount() != null &&
                    tx.getDebitAccount().getId().equals(accountId)) {
                openingBalance = openingBalance.add(tx.getAmount());
            }

            if (tx.getCreditAccount() != null &&
                    tx.getCreditAccount().getId().equals(accountId)) {
                openingBalance = openingBalance.subtract(tx.getAmount());
            }
        }
    }

    // ✅ STEP 2: Current Transactions
    List<LedgerTransaction> transactions =
            transactionRepository.findByAccountAndDateBetween(
                    accountId, fromDate, toDate);

    List<LedgerStatementRow> statement = new ArrayList<>();

    BigDecimal runningBalance = openingBalance;

    // ✅ OPTIONAL: Show opening row
    if (fromDate != null) {
        statement.add(new LedgerStatementRow(
                fromDate.minusDays(1),
                "Opening Balance",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                openingBalance
        ));
    }

    // ✅ STEP 3: Process transactions
    for (LedgerTransaction tx : transactions) {

        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;

        if (tx.getDebitAccount() != null &&
                tx.getDebitAccount().getId().equals(accountId)) {
            debit = tx.getAmount();
            runningBalance = runningBalance.add(debit);
        }

        if (tx.getCreditAccount() != null &&
                tx.getCreditAccount().getId().equals(accountId)) {
            credit = tx.getAmount();
            runningBalance = runningBalance.subtract(credit);
        }

        statement.add(new LedgerStatementRow(
                tx.getTransactionDate(),
                tx.getNarration(),
                debit,
                credit,
                runningBalance
        ));
    }

    return statement;
}

}