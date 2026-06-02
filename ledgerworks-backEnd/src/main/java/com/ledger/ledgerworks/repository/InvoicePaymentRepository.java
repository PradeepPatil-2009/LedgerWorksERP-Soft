package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.InvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoicePaymentRepository
        extends JpaRepository<InvoicePayment, Long> {

    List<InvoicePayment> findByInvoiceId(Long invoiceId);
}