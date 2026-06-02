package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Customer;
import com.ledger.ledgerworks.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Persists a Customer through {@link CustomerService} (which also creates the
 * matching ledger account) and reads it back via the repository.
 */
@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceIT {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void savesAndReadsBackCustomer() {
        Customer customer = new Customer();
        customer.setName("Acme Traders");
        customer.setEmail("acme@example.com");
        customer.setPhone("9999999999");
        customer.setGstNumber("27ABCDE1234F1Z5");

        Customer saved = customerService.create(customer);

        assertNotNull(saved.getId());

        Optional<Customer> reloaded = customerRepository.findById(saved.getId());
        assertTrue(reloaded.isPresent());
        assertEquals("Acme Traders", reloaded.get().getName());
        assertEquals("acme@example.com", reloaded.get().getEmail());

        // Search path should also locate it by name.
        assertTrue(customerService.search("Acme").stream()
                .anyMatch(c -> c.getId().equals(saved.getId())));
    }
}
