package com.ledger.ledgerworks.configuration;

import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Seeds the standard chart of accounts that every document-posting flow relies
 * on (Sales, Purchases, the GST control accounts, Cash / Bank, the debtor /
 * creditor control accounts and a Round Off account).
 *
 * <p>Idempotent and additive: each account is seeded only when it is absent, so
 * accounts created via Customer/Vendor management (or by a prior run) survive
 * restarts and are never overwritten.</p>
 */
@Configuration
public class ChartOfAccountsInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(ChartOfAccountsInitializer.class);

    // accountName -> AccountType for the standard ledger.
    private static final Object[][] ACCOUNTS = {
            {"Sales", AccountType.INCOME},
            {"Purchases", AccountType.EXPENSE},
            {"Sundry Debtors", AccountType.ASSET},
            {"Sundry Creditors", AccountType.LIABILITY},
            {"Cash", AccountType.ASSET},
            {"Bank", AccountType.ASSET},
            {"Output CGST", AccountType.LIABILITY},
            {"Output SGST", AccountType.LIABILITY},
            {"Output IGST", AccountType.LIABILITY},
            {"Input CGST", AccountType.ASSET},
            {"Input SGST", AccountType.ASSET},
            {"Input IGST", AccountType.ASSET},
            {"Round Off", AccountType.EXPENSE},
    };

    @Bean
    CommandLineRunner seedChartOfAccounts(LedgerAccountRepository repo) {
        return args -> {
            int created = 0;

            for (Object[] row : ACCOUNTS) {

                String name = (String) row[0];
                AccountType type = (AccountType) row[1];

                if (repo.existsByAccountName(name)) {
                    continue;
                }

                LedgerAccount account = new LedgerAccount();
                account.setAccountName(name);
                account.setAccountType(type);
                account.setBalance(BigDecimal.ZERO);
                repo.save(account);
                created++;
            }

            if (created > 0) {
                log.info("Seeded {} standard ledger accounts into chart of accounts", created);
            }
        };
    }
}
