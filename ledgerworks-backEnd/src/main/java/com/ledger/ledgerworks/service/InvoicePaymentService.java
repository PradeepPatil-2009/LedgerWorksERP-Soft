package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.InvoicePayment;
import com.ledger.ledgerworks.repository.InvoicePaymentRepository;
import com.ledger.ledgerworks.repository.InvoiceRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class InvoicePaymentService {

    private final InvoicePaymentRepository invoicePaymentRepository;

    private final InvoiceRepository invoiceRepository;

    public InvoicePaymentService(
            InvoicePaymentRepository invoicePaymentRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.invoicePaymentRepository = invoicePaymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // =========================================
    // SAVE PAYMENT
    // =========================================

    @Transactional
    public void payInvoice(
            String invoiceNumber,
            BigDecimal amount,
            String paymentMode,
            String reference
    ) {

        // GUARD: amount must be present and strictly positive

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        Invoice invoice = invoiceRepository
                .findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));

        BigDecimal alreadyPaid = invoicePaymentRepository
                .findByInvoiceId(invoice.getId())
                .stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalPaid =
                alreadyPaid.add(amount);

        if (newTotalPaid.compareTo(invoice.getGrandTotal()) > 0) {

            throw new RuntimeException(
                    "Payment exceeds outstanding amount");
        }

        // SAVE PAYMENT

        InvoicePayment payment = new InvoicePayment();

        payment.setInvoice(invoice);

        payment.setAmount(amount);

        payment.setPaymentMode(paymentMode);

        payment.setReferenceNumber(reference);

        payment.setPaymentDate(LocalDate.now());

        invoicePaymentRepository.save(payment);

        // UPDATE INVOICE

        invoice.setPaidAmount(newTotalPaid);

        BigDecimal outstanding =
                invoice.getGrandTotal().subtract(newTotalPaid);

        invoice.setOutstandingAmount(outstanding);

        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {

            invoice.setPaymentStatus("PAID");

            invoice.setPaidStatus("PAID");

        } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {

            invoice.setPaymentStatus("PARTIAL");

            invoice.setPaidStatus("PARTIAL");

        } else {

            invoice.setPaymentStatus("UNPAID");

            invoice.setPaidStatus("UNPAID");
        }

        invoiceRepository.save(invoice);
    }
}