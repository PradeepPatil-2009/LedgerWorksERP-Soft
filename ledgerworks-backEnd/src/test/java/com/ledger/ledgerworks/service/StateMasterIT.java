package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.StateMaster;
import com.ledger.ledgerworks.repository.StateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Confirms the GST state master is seeded on startup: the repository is
 * populated and the active list (served by GET /api/states) holds > 25 states.
 */
@SpringBootTest
@ActiveProfiles("test")
class StateMasterIT {

    @Autowired
    private StateService stateService;

    @Autowired
    private StateRepository stateRepository;

    @Test
    void seededStatesArePresent() {
        assertTrue(stateRepository.count() > 0, "state master should be seeded");

        List<StateMaster> active = stateService.getAllActive();
        assertTrue(active.size() > 25,
                "expected more than 25 active states, found " + active.size());

        // Spot check a well-known GST code -> state mapping.
        assertTrue(active.stream()
                        .anyMatch(s -> "27".equals(s.getStateCode())
                                && "Maharashtra".equals(s.getStateName())),
                "expected Maharashtra (code 27) among seeded states");
    }
}
