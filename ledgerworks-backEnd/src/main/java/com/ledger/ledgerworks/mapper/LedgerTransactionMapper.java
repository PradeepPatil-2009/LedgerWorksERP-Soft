package com.ledger.ledgerworks.mapper;

import com.ledger.ledgerworks.dto.LedgerTransactionRequest;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.LedgerTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LedgerTransactionMapper {

    public static LedgerTransaction toEntity(
            LedgerTransactionRequest request,
            LedgerAccount debitAccount,
            LedgerAccount creditAccount,
            String transactionNumber,
            String user
    ) {

        LedgerTransaction transaction = new LedgerTransaction();

        transaction.setTransactionNumber(transactionNumber);
        transaction.setDebitAccount(debitAccount);
        transaction.setCreditAccount(creditAccount);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getNarration());
        transaction.setTransactionDate(LocalDate.now());

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCreatedBy(user);

        return transaction;
    }
}