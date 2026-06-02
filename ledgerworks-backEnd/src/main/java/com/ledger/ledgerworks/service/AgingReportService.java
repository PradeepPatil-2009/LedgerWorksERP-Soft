package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.CustomerAgingRow;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgingReportService {

    private final InvoiceRepository invoiceRepository;

    public AgingReportService(
            InvoiceRepository invoiceRepository
    ) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<CustomerAgingRow>
    generateCustomerWiseAging() {

        List<Invoice> invoices =
                invoiceRepository.findAll();

        Map<String, List<Invoice>> grouped =
                invoices.stream()

                // ✅ NULL SAFE FILTER
                .filter(i -> {

                    BigDecimal outstanding =
                            i.getOutstandingAmount() == null
                                    ? BigDecimal.ZERO
                                    : i.getOutstandingAmount();

                    return outstanding.compareTo(
                            BigDecimal.ZERO
                    ) > 0;
                })

                .collect(
                        Collectors.groupingBy(
                                Invoice::getCustomerName
                        )
                );

        List<CustomerAgingRow> result =
                new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (
                Map.Entry<String,
                List<Invoice>> entry
                        : grouped.entrySet()
        ) {

            BigDecimal bucket0To30 =
                    BigDecimal.ZERO;

            BigDecimal bucket31To60 =
                    BigDecimal.ZERO;

            BigDecimal bucket61To90 =
                    BigDecimal.ZERO;

            BigDecimal bucket90Plus =
                    BigDecimal.ZERO;

            for (
                    Invoice invoice :
                    entry.getValue()
            ) {

                long days =
                        ChronoUnit.DAYS.between(
                                invoice.getInvoiceDate(),
                                today
                        );

                // ✅ NULL SAFE
                BigDecimal outstanding =
                        invoice.getOutstandingAmount() == null
                                ? BigDecimal.ZERO
                                : invoice.getOutstandingAmount();

                if (days <= 30) {

                    bucket0To30 =
                            bucket0To30.add(
                                    outstanding
                            );

                } else if (days <= 60) {

                    bucket31To60 =
                            bucket31To60.add(
                                    outstanding
                            );

                } else if (days <= 90) {

                    bucket61To90 =
                            bucket61To90.add(
                                    outstanding
                            );

                } else {

                    bucket90Plus =
                            bucket90Plus.add(
                                    outstanding
                            );
                }
            }

            BigDecimal total =
                    bucket0To30
                            .add(bucket31To60)
                            .add(bucket61To90)
                            .add(bucket90Plus);

            result.add(

                    new CustomerAgingRow(

                            entry.getKey(),

                            bucket0To30,

                            bucket31To60,

                            bucket61To90,

                            bucket90Plus,

                            total
                    )
            );
        }

        return result;
    }
}