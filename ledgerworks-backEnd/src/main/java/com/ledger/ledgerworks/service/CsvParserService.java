package com.ledger.ledgerworks.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses a CSV upload into a list of header-keyed row maps.
 *
 * The first non-blank line is treated as the header. Header keys are
 * lower-cased and trimmed so callers can look them up case-insensitively.
 */
@Service
public class CsvParserService {

    public List<Map<String, String>> parse(MultipartFile file) throws IOException {

        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();

            if (headerLine == null) {
                return rows;
            }

            // Strip a leading UTF-8 BOM if present.
            if (headerLine.startsWith("﻿")) {
                headerLine = headerLine.substring(1);
            }

            String[] headers = splitCsvLine(headerLine);

            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim().toLowerCase();
            }

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = splitCsvLine(line);

                Map<String, String> row = new LinkedHashMap<>();

                for (int i = 0; i < headers.length; i++) {

                    String value =
                            i < values.length
                                    ? values[i].trim()
                                    : "";

                    row.put(headers[i], value);
                }

                rows.add(row);
            }
        }

        return rows;
    }

    // =====================================================
    // Minimal CSV splitter supporting quoted fields and
    // escaped double-quotes ("") inside quoted values.
    // =====================================================

    private String[] splitCsvLine(String line) {

        List<String> result = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                if (inQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    current.append('"');
                    i++;

                } else {

                    inQuotes = !inQuotes;
                }

            } else if (c == ',' && !inQuotes) {

                result.add(current.toString());
                current.setLength(0);

            } else {

                current.append(c);
            }
        }

        result.add(current.toString());

        return result.toArray(new String[0]);
    }
}
