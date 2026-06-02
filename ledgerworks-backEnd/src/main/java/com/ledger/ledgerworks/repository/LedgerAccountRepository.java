/*package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.dto.OutstandingRow;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {

    // =========================================
    // FIND BY ACCOUNT NAME
    // =========================================
    Optional<LedgerAccount> findByAccountName(String accountName);

    boolean existsByAccountName(String accountName);


    // =========================================
    // FIND BY ACCOUNT TYPE
    // =========================================
    List<LedgerAccount> findByAccountType(AccountType accountType);


    // =========================================
    // OUTSTANDING CUSTOMERS REPORT
    // =========================================
    @Query("""
        SELECT new com.ledger.ledgerworks.dto.OutstandingRow(
            a.id,
            a.accountName,
            a.balance
        )
        FROM LedgerAccount a
        WHERE a.accountType = com.ledger.ledgerworks.enums.AccountType.ASSET
        AND a.accountName <> 'Bank'
        AND a.balance > 0
    """)
    List<OutstandingRow> findOutstandingCustomers();


    // =========================================
    // TOTAL BALANCE BY TYPE
    // =========================================
    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM LedgerAccount a
        WHERE a.accountType = :accountType
    """)
    BigDecimal getTotalBalanceByType(@Param("accountType") AccountType accountType);


    // =========================================
    // TOTAL RECEIVABLES
    // =========================================
    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM LedgerAccount a
        WHERE a.accountType = com.ledger.ledgerworks.enums.AccountType.ASSET
        AND a.accountName <> 'Bank'
        AND a.balance > 0
    """)
    BigDecimal getTotalOutstanding();
}*/


package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.dto.OutstandingRow;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {

    // =========================================
    // FIND BY ACCOUNT NAME
    // =========================================
    Optional<LedgerAccount> findByAccountName(String accountName);

    boolean existsByAccountName(String accountName);


    // =========================================
    // FIND BY ACCOUNT TYPE
    // =========================================
    List<LedgerAccount> findByAccountType(AccountType accountType);


    // =========================================
    // GET ALL WITH SORTING (NEW - UI FRIENDLY)
    // =========================================
    default List<LedgerAccount> findAllSortedByName() {
        return findAll(Sort.by(Sort.Direction.ASC, "accountName"));
    }


    // =========================================
    // OUTSTANDING CUSTOMERS REPORT
    // =========================================
  /*  @Query("""
        SELECT new com.ledger.ledgerworks.dto.OutstandingRow(
            a.id,
            a.accountName,
            a.balance
        )
        FROM LedgerAccount a
        WHERE a.accountType = com.ledger.ledgerworks.enums.AccountType.ASSET
        AND a.accountName <> 'Bank'
        AND a.balance > 0
    """)
    List<OutstandingRow> findOutstandingCustomers();
    
    */


    // =========================================
    // TOTAL BALANCE BY TYPE
    // =========================================
    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM LedgerAccount a
        WHERE a.accountType = :accountType
    """)
    BigDecimal getTotalBalanceByType(@Param("accountType") AccountType accountType);


    // =========================================
    // TOTAL RECEIVABLES
    // =========================================
    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM LedgerAccount a
        WHERE a.accountType = com.ledger.ledgerworks.enums.AccountType.ASSET
        AND a.accountName <> 'Bank'
        AND a.balance > 0
    """)
    BigDecimal getTotalOutstanding();
}