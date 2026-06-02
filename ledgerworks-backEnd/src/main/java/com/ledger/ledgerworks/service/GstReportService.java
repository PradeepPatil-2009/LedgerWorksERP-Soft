package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.GstSummaryDto;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledger.ledgerworks.dto.GstDetailDto;
import com.ledger.ledgerworks.entity.Invoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GstReportService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public GstSummaryDto getSummary(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        return invoiceRepository.getGstSummary(
                fromDate,
                toDate
        );
    }
    
    public List<GstDetailDto> getDetailedReport(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        List<Invoice> invoices =
                invoiceRepository.findInvoicesForGstReport(
                        fromDate,
                        toDate
                );

        List<GstDetailDto> result =
                new ArrayList<>();

        for (Invoice invoice : invoices) {

            GstDetailDto dto =
                    new GstDetailDto();

            dto.setInvoiceNumber(
                    invoice.getInvoiceNumber()
            );

            dto.setInvoiceDate(
                    invoice.getInvoiceDate()
            );

            dto.setCustomerName(
                    invoice.getCustomerName()
            );

            dto.setCustomerState(
                    invoice.getCustomerState()
            );

            dto.setTaxable(
                    invoice.getTotalTaxable()
            );

            dto.setCgst(
                    invoice.getTotalCGST()
            );

            dto.setSgst(
                    invoice.getTotalSGST()
            );

            dto.setIgst(
                    invoice.getTotalIGST()
            );

            dto.setTotal(
                    invoice.getGrandTotal()
            );

            result.add(dto);
        }

        return result;
    }
}