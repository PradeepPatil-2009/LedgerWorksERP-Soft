package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.GstRate;
import com.ledger.ledgerworks.repository.GstRateRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GstRateService {

    private final GstRateRepository repository;

    public GstRateService(GstRateRepository repository) {
        this.repository = repository;
    }

    // =====================================================
    // LIST ACTIVE (used to populate rate pickers on forms)
    // =====================================================

    public List<GstRate> listActive() {
        return repository.findByActiveTrueOrderByRateAsc();
    }

    // =====================================================
    // CRUD
    // =====================================================

    public GstRate create(GstRate rate) {
        rate.setId(null);
        return repository.save(rate);
    }

    public GstRate update(Long id, GstRate rate) {
        rate.setId(id);
        return repository.save(rate);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
