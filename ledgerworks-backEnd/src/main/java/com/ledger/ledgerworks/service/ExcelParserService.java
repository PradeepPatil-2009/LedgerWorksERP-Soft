package com.ledger.ledgerworks.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses an .xlsx upload (Apache POI) into a list of header-keyed row maps.
 *
 * The first row of the first sheet is treated as the header. Header keys are
 * lower-cased and trimmed. A DataFormatter is used so numeric/date cells come
 * back as the same text the user sees in Excel.
 *
 * Upload DoS hardening: parsing is capped at {@link #MAX_ROWS} data rows and
 * {@link #MAX_COLS} columns. A file exceeding either limit is rejected with a
 * clear {@link IOException} rather than streaming an unbounded amount of data
 * into memory. Callers catch this and turn it into a skipped/error summary.
 */
@Service
public class ExcelParserService {

    /** Hard cap on the number of data rows accepted from a single upload. */
    static final int MAX_ROWS = 50_000;

    /** Hard cap on the number of columns accepted from a single upload. */
    static final int MAX_COLS = 100;

    private final DataFormatter formatter = new DataFormatter();

    public List<Map<String, String>> parse(MultipartFile file) throws IOException {

        List<Map<String, String>> rows = new ArrayList<>();

        try (InputStream in = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getNumberOfSheets() > 0
                    ? workbook.getSheetAt(0)
                    : null;

            if (sheet == null) {
                return rows;
            }

            Row headerRow = sheet.getRow(sheet.getFirstRowNum());

            if (headerRow == null) {
                return rows;
            }

            List<String> headers = new ArrayList<>();

            int lastCol = headerRow.getLastCellNum();

            // Reject absurdly wide sheets before allocating per-column state.
            if (lastCol > MAX_COLS) {
                throw new IOException(
                        "Upload rejected: too many columns ("
                                + lastCol + ", limit " + MAX_COLS + ")");
            }

            for (int c = 0; c < lastCol; c++) {

                Cell cell = headerRow.getCell(c);

                headers.add(
                        cell == null
                                ? ""
                                : formatter.formatCellValue(cell)
                                        .trim()
                                        .toLowerCase());
            }

            for (int r = headerRow.getRowNum() + 1;
                 r <= sheet.getLastRowNum();
                 r++) {

                // Stop before memory grows unbounded on a hostile upload.
                if (rows.size() >= MAX_ROWS) {
                    throw new IOException(
                            "Upload rejected: too many rows (limit "
                                    + MAX_ROWS + ")");
                }

                Row row = sheet.getRow(r);

                if (row == null) {
                    continue;
                }

                Map<String, String> rowMap = new LinkedHashMap<>();

                boolean blankRow = true;

                for (int c = 0; c < headers.size(); c++) {

                    String header = headers.get(c);

                    if (header.isEmpty()) {
                        continue;
                    }

                    Cell cell = row.getCell(c);

                    String value =
                            cell == null
                                    ? ""
                                    : formatter.formatCellValue(cell).trim();

                    if (!value.isEmpty()) {
                        blankRow = false;
                    }

                    rowMap.put(header, value);
                }

                if (!blankRow) {
                    rows.add(rowMap);
                }
            }
        }

        return rows;
    }

    /**
     * CSV/Excel formula-injection guard for OUTPUT export.
     *
     * If {@code v} is non-empty and its first character is one of the cell
     * "trigger" characters ({@code = + - @}, tab or carriage return), the value
     * is prefixed with a single quote so spreadsheet software treats it as text
     * instead of evaluating it as a formula. Otherwise the value is returned
     * unchanged.
     *
     * These parser services only READ uploads, so there is no write site here
     * to apply this to; the actual export write path lives in
     * {@code GstExportController}. The helper is provided for the export path
     * that emits user-supplied cell values.
     */
    private static String neutralize(String v) {

        if (v == null || v.isEmpty()) {
            return v;
        }

        char first = v.charAt(0);

        if (first == '=' || first == '+' || first == '-'
                || first == '@' || first == '\t' || first == '\r') {
            return "'" + v;
        }

        return v;
    }
}
