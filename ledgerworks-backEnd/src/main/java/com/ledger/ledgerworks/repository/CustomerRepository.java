package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    List<Customer>
    findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email
    );

    Optional<Customer> findByGstNumber(String gstNumber);

    Optional<Customer> findByName(String name);
}