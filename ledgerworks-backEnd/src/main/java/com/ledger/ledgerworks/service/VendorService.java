package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Vendor;
import com.ledger.ledgerworks.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorService {

    private final VendorRepository repository;

    public VendorService(VendorRepository repository) {
        this.repository = repository;
    }

    // ✅ CREATE (with duplicate check)
    public Vendor create(Vendor vendor) {

        Optional<Vendor> existing = repository.findByEmail(vendor.getEmail());

        if (existing.isPresent()) {
            throw new RuntimeException("Vendor already exists with this email");
        }

        return repository.save(vendor);
    }

    // ✅ GET ALL
    public List<Vendor> getAll() {
        return repository.findAll();
    }

    // ✅ DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Vendor not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // ✅ UPDATE
    public Vendor update(Long id, Vendor vendor) {

        Vendor existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        existing.setName(vendor.getName());
        existing.setEmail(vendor.getEmail());
        existing.setPhone(vendor.getPhone());
        existing.setAddress(vendor.getAddress());
        existing.setGstNumber(vendor.getGstNumber());

        return repository.save(existing);
    }
}