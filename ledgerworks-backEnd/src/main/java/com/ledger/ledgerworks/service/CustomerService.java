package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Customer;
import com.ledger.ledgerworks.entity.LedgerAccount;

import com.ledger.ledgerworks.repository.CustomerRepository;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;
import com.ledger.ledgerworks.enums.AccountType;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    private final LedgerAccountRepository ledgerAccountRepository;

    public CustomerService(

            CustomerRepository repository,

            LedgerAccountRepository ledgerAccountRepository
    ) {

        this.repository = repository;

        this.ledgerAccountRepository =
                ledgerAccountRepository;
    }

    public Customer create(Customer customer) {

        Customer savedCustomer =
                repository.save(customer);

        // CREATE CUSTOMER LEDGER ACCOUNT

        LedgerAccount ledger =
                new LedgerAccount();

        ledger.setAccountName(
                customer.getName()
        );

        ledger.setAccountType(
                AccountType.ASSET
        );

        ledger.setBalance(
                BigDecimal.ZERO
        );

        ledgerAccountRepository.save(
                ledger
        );

        return savedCustomer;
    }

    public List<Customer> getAll() {

        return repository.findAll();
    }

    // SEARCH

    public List<Customer> search(String keyword) {

        return repository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword
                );
    }

    public Customer update(
            Long id,
            Customer customer
    ) {

        Customer existing =
                repository.findById(id)
                        .orElseThrow();

        existing.setName(
                customer.getName()
        );

        existing.setEmail(
                customer.getEmail()
        );

        existing.setPhone(
                customer.getPhone()
        );

        existing.setAddress(
                customer.getAddress()
        );

        return repository.save(existing);
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }
}