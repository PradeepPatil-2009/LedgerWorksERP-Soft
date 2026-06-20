package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.FinancialYear;
import com.ledger.ledgerworks.repository.FinancialYearRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FinancialYearService {

    private final FinancialYearRepository repository;

    public FinancialYearService(FinancialYearRepository repository) {
        this.repository = repository;
    }

    // ✅ SEED CURRENT FINANCIAL YEAR IF EMPTY (April -> March)
    @PostConstruct
    public void seedDefaults() {

        if (repository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        int startYear = today.getMonthValue() >= 4 ? today.getYear() : today.getYear() - 1;
        int endYear = startYear + 1;

        FinancialYear fy = new FinancialYear();
        fy.setName(startYear + "-" + endYear);
        fy.setStartDate(LocalDate.of(startYear, 4, 1));
        fy.setEndDate(LocalDate.of(endYear, 3, 31));
        fy.setActive(true);
        fy.setLocked(false);

        repository.save(fy);
    }

    // ✅ GET ALL (newest first)
    public List<FinancialYear> getAll() {
        return repository.findAllByOrderByStartDateDesc();
    }

    // ✅ GET ACTIVE
    public FinancialYear getActive() {
        return repository.findByActiveTrue().orElse(null);
    }

    // ✅ CREATE
    public FinancialYear create(FinancialYear fy) {

        if (fy.getName() == null || fy.getName().isBlank()) {
            throw new RuntimeException("Financial year name is required");
        }

        Optional<FinancialYear> existing = repository.findByName(fy.getName().trim());
        if (existing.isPresent()) {
            throw new RuntimeException("Financial year already exists with this name");
        }

        fy.setName(fy.getName().trim());
        fy.setLocked(false);

        // First financial year becomes active automatically.
        if (repository.count() == 0) {
            fy.setActive(true);
        } else if (fy.isActive()) {
            clearActiveFlags();
        }

        return repository.save(fy);
    }

    // ✅ UPDATE (dates/name; not the active/locked flags - those have dedicated endpoints)
    public FinancialYear update(Long id, FinancialYear fy) {

        FinancialYear existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Financial year not found"));

        if (existing.isLocked()) {
            throw new RuntimeException("Cannot update a locked financial year");
        }

        if (fy.getName() != null && !fy.getName().isBlank()) {
            existing.setName(fy.getName().trim());
        }
        existing.setStartDate(fy.getStartDate());
        existing.setEndDate(fy.getEndDate());

        return repository.save(existing);
    }

    // ✅ ACTIVATE (exclusive)
    @Transactional
    public FinancialYear setActive(Long id) {

        FinancialYear target = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Financial year not found"));

        clearActiveFlags();

        target.setActive(true);
        return repository.save(target);
    }

    // ✅ LOCK
    public FinancialYear lock(Long id) {

        FinancialYear target = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Financial year not found"));

        target.setLocked(true);
        return repository.save(target);
    }

    private void clearActiveFlags() {

        List<FinancialYear> all = repository.findAll();
        for (FinancialYear y : all) {
            if (y.isActive()) {
                y.setActive(false);
                repository.save(y);
            }
        }
    }
}
