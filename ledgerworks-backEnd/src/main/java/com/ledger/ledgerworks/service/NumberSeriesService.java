package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.NumberSeries;
import com.ledger.ledgerworks.enums.DocumentType;
import com.ledger.ledgerworks.repository.NumberSeriesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NumberSeriesService {

    private final NumberSeriesRepository repository;

    public NumberSeriesService(NumberSeriesRepository repository) {
        this.repository = repository;
    }

    // ✅ SEED DEFAULTS IF EMPTY
    @PostConstruct
    public void seedDefaults() {

        if (repository.count() > 0) {
            return;
        }

        for (DocumentType type : DocumentType.values()) {

            NumberSeries series = new NumberSeries();
            series.setDocumentType(type);
            series.setPrefix(defaultPrefix(type));
            series.setCurrentNumber(0);
            series.setPadding(4);

            repository.save(series);
        }
    }

    private String defaultPrefix(DocumentType type) {

        switch (type) {
            case INVOICE:
                return "INV";
            case PURCHASE:
                return "PUR";
            case DELIVERY_CHALLAN:
                return "DC";
            case RECEIPT:
                return "RCP";
            case PAYMENT:
                return "PAY";
            case JOURNAL:
                return "JV";
            case CREDIT_NOTE:
                return "CN";
            case DEBIT_NOTE:
                return "DN";
            case CONTRA:
                return "CON";
            default:
                return type.name();
        }
    }

    // ✅ GET ALL
    public List<NumberSeries> getAll() {
        return repository.findAll();
    }

    // ✅ UPSERT (create or update by document type)
    public NumberSeries upsert(NumberSeries input) {

        if (input.getDocumentType() == null) {
            throw new RuntimeException("Document type is required");
        }

        NumberSeries series = repository
                .findByDocumentType(input.getDocumentType())
                .orElseGet(NumberSeries::new);

        series.setDocumentType(input.getDocumentType());
        series.setPrefix(input.getPrefix());
        series.setCurrentNumber(input.getCurrentNumber());

        int padding = input.getPadding() > 0 ? input.getPadding() : 4;
        series.setPadding(padding);

        return repository.save(series);
    }

    // ✅ NEXT NUMBER -> prefix + zero-padded incremented number (persisted)
    @Transactional
    public String next(DocumentType documentType) {

        NumberSeries series = repository
                .findByDocumentType(documentType)
                .orElseGet(() -> {

                    NumberSeries created = new NumberSeries();
                    created.setDocumentType(documentType);
                    created.setPrefix(defaultPrefix(documentType));
                    created.setCurrentNumber(0);
                    created.setPadding(4);

                    return created;
                });

        long nextNumber = series.getCurrentNumber() + 1;
        series.setCurrentNumber(nextNumber);

        repository.save(series);

        int padding = series.getPadding() > 0 ? series.getPadding() : 4;
        String prefix = series.getPrefix() != null ? series.getPrefix() : "";

        return prefix + String.format("%0" + padding + "d", nextNumber);
    }
}
