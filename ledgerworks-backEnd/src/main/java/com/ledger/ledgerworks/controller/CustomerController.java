package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Customer;
import com.ledger.ledgerworks.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // SAVE
    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return service.create(customer);
    }

    // GET ALL
    @GetMapping
    public List<Customer> getAll() {
        return service.getAll();
    }

    // SEARCH
    @GetMapping("/search")
    public List<Customer> search(
            @RequestParam String keyword
    ) {
        return service.search(keyword);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Customer update(
            @PathVariable Long id,
            @RequestBody Customer customer
    ) {
        return service.update(id, customer);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}