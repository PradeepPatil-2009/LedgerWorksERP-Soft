package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.OutstandingRow;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutstandingService {

    private final InvoiceRepository repository;

    public OutstandingService(
            InvoiceRepository repository
    ) {
        this.repository = repository;
    }

    public List<OutstandingRow> getOutstandingReport() {

        return repository.findOutstandingCustomers();
    }
}