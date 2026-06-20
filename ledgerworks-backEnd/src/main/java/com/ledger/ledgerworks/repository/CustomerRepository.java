package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Paginated free-text search across the obvious text columns
    // (case-insensitive substring). Ordering comes from the Pageable.
    @Query("""
            SELECT c FROM Customer c
            WHERE LOWER(COALESCE(c.name, ''))      LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(c.email, ''))     LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(c.gstNumber, '')) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(c.state, ''))     LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(c.phone, ''))     LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<Customer> searchPage(@Param("q") String q, Pageable pageable);
}
