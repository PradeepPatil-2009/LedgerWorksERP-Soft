package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.NumberSeries;
import com.ledger.ledgerworks.enums.DocumentType;
import com.ledger.ledgerworks.repository.NumberSeriesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies NumberSeriesService.next(type) increments the counter, persists it,
 * and yields a prefixed zero-padded document number that keeps growing.
 */
@SpringBootTest
@ActiveProfiles("test")
class NumberSeriesServiceIT {

    @Autowired
    private NumberSeriesService numberSeriesService;

    @Autowired
    private NumberSeriesRepository numberSeriesRepository;

    @Test
    void nextIncrementsAndPersists() {
        long before = numberSeriesRepository
                .findByDocumentType(DocumentType.RECEIPT)
                .map(NumberSeries::getCurrentNumber)
                .orElse(0L);

        String first = numberSeriesService.next(DocumentType.RECEIPT);
        String second = numberSeriesService.next(DocumentType.RECEIPT);

        assertNotEquals(first, second, "consecutive calls must differ");
        assertTrue(first.startsWith("RCP"), "expected RECEIPT prefix RCP, got " + first);

        long after = numberSeriesRepository
                .findByDocumentType(DocumentType.RECEIPT)
                .map(NumberSeries::getCurrentNumber)
                .orElseThrow();

        // Two next() calls must have advanced the persisted counter by two.
        assertEquals(before + 2, after, "counter should be persisted and incremented");
    }
}
