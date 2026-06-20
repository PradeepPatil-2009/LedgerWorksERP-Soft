package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Customer;
import com.ledger.ledgerworks.repository.CustomerRepository;
import com.ledger.ledgerworks.service.CustomerService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    private final CustomerRepository repository;

    public CustomerController(
            CustomerService service,
            CustomerRepository repository
    ) {
        this.service = service;
        this.repository = repository;
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

    // GET ALL (server-paginated). Optional sort (e.g. "id,desc"; default id
    // DESC so newly-created rows appear first) and free-text q across the
    // obvious text columns. Returns a Spring Data Page<Customer>.
    @GetMapping("/page")
    public Page<Customer> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String q
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        if (q == null || q.isBlank()) {
            return repository.findAll(pageable);
        }

        return repository.searchPage(q.trim(), pageable);
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

    // Parse a "field,dir" sort param into a Sort. Defaults to id DESC so the
    // newest rows surface first.
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Order.desc("id"));
        }

        String[] parts = sort.split(",");
        String field = parts[0].trim();
        if (field.isEmpty()) {
            field = "id";
        }

        Sort.Direction direction =
                (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(direction, field);
    }
}
