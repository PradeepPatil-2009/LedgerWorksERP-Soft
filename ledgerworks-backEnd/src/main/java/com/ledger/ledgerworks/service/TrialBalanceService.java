package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;
import com.ledger.ledgerworks.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TrialBalanceService {

private final LedgerTransactionRepository transactionRepository;

public TrialBalanceService(
        LedgerTransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
}

public TrialBalanceResponse generateTrialBalance(
        LocalDate fromDate,
        LocalDate toDate) {

    List<TrialBalanceRow> rows =
            transactionRepository.getTrialBalanceWithDate(fromDate, toDate);

    BigDecimal totalDebit = BigDecimal.ZERO;
    BigDecimal totalCredit = BigDecimal.ZERO;

    for (TrialBalanceRow row : rows) {

        BigDecimal debit = row.getDebit() != null ? row.getDebit() : BigDecimal.ZERO;
        BigDecimal credit = row.getCredit() != null ? row.getCredit() : BigDecimal.ZERO;

        totalDebit = totalDebit.add(debit);
        totalCredit = totalCredit.add(credit);
    }

    boolean balanced =
            totalDebit.compareTo(totalCredit) == 0;

    return new TrialBalanceResponse(
            rows,
            totalDebit,
            totalCredit,
            balanced
    );
}

}