package com.ledger.ledgerworks.configuration;

import com.ledger.ledgerworks.entity.GstRate;
import com.ledger.ledgerworks.repository.GstRateRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the standard Indian GST slabs (0 / 5 / 12 / 18 / 28%) on first run.
 * Idempotent: only runs when the table is empty.
 */
@Configuration
public class GstRateInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(GstRateInitializer.class);

    private static final int[] SLABS = {0, 5, 12, 18, 28};

    @Bean
    CommandLineRunner seedGstRates(GstRateRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }

            List<GstRate> toSave = new ArrayList<>();

            for (int slab : SLABS) {
                GstRate rate = new GstRate();
                rate.setRate(BigDecimal.valueOf(slab));
                rate.setLabel("GST " + slab + "%");
                rate.setActive(true);
                toSave.add(rate);
            }

            repo.saveAll(toSave);

            log.info("Seeded {} GST rate slabs into gst_rate master", toSave.size());
        };
    }
}
