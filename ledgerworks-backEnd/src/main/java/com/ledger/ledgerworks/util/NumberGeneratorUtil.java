package com.ledger.ledgerworks.util;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class NumberGeneratorUtil {

    public String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        return "INV/" + year + "/" + UUID.randomUUID().toString().substring(0, 5);
    }
}