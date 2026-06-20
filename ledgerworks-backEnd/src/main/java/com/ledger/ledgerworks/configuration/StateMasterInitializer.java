package com.ledger.ledgerworks.configuration;

import com.ledger.ledgerworks.entity.StateMaster;
import com.ledger.ledgerworks.repository.StateRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the GST state master (Indian states + UTs, codes 01..38) on first
 * run. Idempotent: only runs when the table is empty.
 */
@Configuration
public class StateMasterInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(StateMasterInitializer.class);

    // GST numeric code -> state / UT name.
    private static final String[][] STATES = {
            {"01", "Jammu and Kashmir"},
            {"02", "Himachal Pradesh"},
            {"03", "Punjab"},
            {"04", "Chandigarh"},
            {"05", "Uttarakhand"},
            {"06", "Haryana"},
            {"07", "Delhi"},
            {"08", "Rajasthan"},
            {"09", "Uttar Pradesh"},
            {"10", "Bihar"},
            {"11", "Sikkim"},
            {"12", "Arunachal Pradesh"},
            {"13", "Nagaland"},
            {"14", "Manipur"},
            {"15", "Mizoram"},
            {"16", "Tripura"},
            {"17", "Meghalaya"},
            {"18", "Assam"},
            {"19", "West Bengal"},
            {"20", "Jharkhand"},
            {"21", "Odisha"},
            {"22", "Chhattisgarh"},
            {"23", "Madhya Pradesh"},
            {"24", "Gujarat"},
            {"25", "Daman and Diu"},
            {"26", "Dadra and Nagar Haveli and Daman and Diu"},
            {"27", "Maharashtra"},
            {"28", "Andhra Pradesh (Old)"},
            {"29", "Karnataka"},
            {"30", "Goa"},
            {"31", "Lakshadweep"},
            {"32", "Kerala"},
            {"33", "Tamil Nadu"},
            {"34", "Puducherry"},
            {"35", "Andaman and Nicobar Islands"},
            {"36", "Telangana"},
            {"37", "Andhra Pradesh"},
            {"38", "Ladakh"},
    };

    @Bean
    CommandLineRunner seedStates(StateRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }

            List<StateMaster> toSave = new ArrayList<>();

            for (String[] row : STATES) {
                StateMaster state = new StateMaster();
                state.setStateCode(row[0]);
                state.setStateName(row[1]);
                state.setActive(true);
                toSave.add(state);
            }

            repo.saveAll(toSave);

            log.info("Seeded {} GST states/UTs into state master", toSave.size());
        };
    }
}
