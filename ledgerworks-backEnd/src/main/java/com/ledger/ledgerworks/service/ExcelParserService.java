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
 */
@Service
public class ExcelParserService {

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
}
