package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.StateMaster;
import com.ledger.ledgerworks.repository.StateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

    @Autowired
    private StateRepository repo;

    // =====================================================
    // GET ALL ACTIVE STATES
    // =====================================================

    public List<StateMaster> getAllActive() {

        return repo.findByActiveTrueOrderByStateCodeAsc();
    }

    // =====================================================
    // CREATE
    // =====================================================

    public StateMaster create(
            StateMaster state
    ) {

        state.setId(null);

        return repo.save(state);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    public StateMaster update(
            Long id,
            StateMaster state
    ) {

        StateMaster existing = repo.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "State not found"
                        )
                );

        existing.setStateName(state.getStateName());

        existing.setStateCode(state.getStateCode());

        existing.setActive(state.isActive());

        return repo.save(existing);
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Long id
    ) {

        repo.deleteById(id);
    }
}
