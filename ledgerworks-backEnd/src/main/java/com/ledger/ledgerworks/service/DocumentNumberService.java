package com.ledger.ledgerworks.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DocumentNumberService {

    // =====================================================
    // COMMON GENERATOR
    // =====================================================

    public String generateNumber(

            String prefix,

            String lastNumber
    ) {

        LocalDate today = LocalDate.now();

        // =========================================
        // FINANCIAL YEAR
        // =========================================

        int year = today.getYear();

        int startYear;
        int endYear;

        if (today.getMonthValue() >= 4) {

            startYear = year % 100;

            endYear = (year + 1) % 100;

        } else {

            startYear = (year - 1) % 100;

            endYear = year % 100;
        }

        String fy =

                String.format(
                        "%02d-%02d",
                        startYear,
                        endYear
                );

        // =========================================
        // MONTH
        // =========================================

        String month =

                today.getMonth()
                        .toString()
                        .substring(0, 3);

        // =========================================
        // DEFAULT
        // =========================================

        char series = 'A';

        int number = 1;

        // =========================================
        // LAST NUMBER PARSE
        // =========================================

        if (

                lastNumber != null

                &&

                lastNumber.startsWith(
                        prefix + "/" + fy
                )
        ) {

            try {

                String[] parts =

                        lastNumber.split("/");

                String serial = parts[3];

                series = serial.charAt(0);

                number = Integer.parseInt(

                        serial.substring(1)
                );

                number++;

                // =============================
                // AFTER 99999
                // =============================

                if (number > 99999) {

                    series++;

                    number = 1;
                }

            } catch (Exception e) {

                series = 'A';

                number = 1;
            }
        }

        // =========================================
        // FINAL FORMAT
        // =========================================

        return prefix
                + "/"
                + fy
                + "/"
                + month
                + "/"
                + series
                + String.format("%05d", number);
    }
}