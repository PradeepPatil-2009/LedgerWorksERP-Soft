package com.ledger.ledgerworks;

import com.ledger.ledgerworks.configuration.StateMasterInitializer;
import com.ledger.ledgerworks.controller.CompanySettingsController;
import com.ledger.ledgerworks.controller.NumberSeriesController;
import com.ledger.ledgerworks.controller.ReceiptVoucherController;
import com.ledger.ledgerworks.controller.StateController;
import com.ledger.ledgerworks.service.CompanySettingsService;
import com.ledger.ledgerworks.service.NumberSeriesService;
import com.ledger.ledgerworks.service.ReceiptVoucherService;
import com.ledger.ledgerworks.service.StateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test confirming the application context still loads with every new
 * module bean (config, services, controllers) and the audit-log aspect wired in.
 */
@SpringBootTest
@ActiveProfiles("test")
class NewModulesContextTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private StateService stateService;

    @Autowired
    private NumberSeriesService numberSeriesService;

    @Autowired
    private ReceiptVoucherService receiptVoucherService;

    @Test
    void newModuleBeansAreWired() {
        assertNotNull(companySettingsService);
        assertNotNull(stateService);
        assertNotNull(numberSeriesService);
        assertNotNull(receiptVoucherService);

        assertNotNull(context.getBean(CompanySettingsController.class));
        assertNotNull(context.getBean(StateController.class));
        assertNotNull(context.getBean(NumberSeriesController.class));
        assertNotNull(context.getBean(ReceiptVoucherController.class));
        assertNotNull(context.getBean(StateMasterInitializer.class));
    }

    @Test
    void auditLogAspectIsPresent() {
        // The audit aspect is a Spring-managed bean; verify it loaded.
        assertTrue(context.getBeanNamesForType(
                com.ledger.ledgerworks.aspect.AuditLogAspect.class).length > 0);
    }
}
