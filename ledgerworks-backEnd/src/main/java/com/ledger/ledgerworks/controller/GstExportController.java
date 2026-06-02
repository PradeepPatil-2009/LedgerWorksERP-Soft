package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/gst")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true"
)
public class GstExportController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate

    ) {

        try {

            List<Invoice> invoices =
                    invoiceRepository
                            .findByInvoiceDateBetween(
                                    fromDate,
                                    toDate
                            );

            XSSFWorkbook workbook =
                    new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet(
                            "GST Report"
                    );

            // ================= HEADER =================

            Row header = sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("Invoice No");

            header.createCell(1)
                    .setCellValue("Date");

            header.createCell(2)
                    .setCellValue("Customer");

            header.createCell(3)
                    .setCellValue("State");

            header.createCell(4)
                    .setCellValue("Taxable");

            header.createCell(5)
                    .setCellValue("CGST");

            header.createCell(6)
                    .setCellValue("SGST");

            header.createCell(7)
                    .setCellValue("IGST");

            header.createCell(8)
                    .setCellValue("Total");

            // ================= DATA =================

            int rowNum = 1;

            for (Invoice invoice : invoices) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(
                                invoice.getInvoiceNumber()
                        );

                row.createCell(1)
                        .setCellValue(
                                invoice.getInvoiceDate()
                                        .toString()
                        );

                row.createCell(2)
                        .setCellValue(
                                invoice.getCustomerName()
                        );

                row.createCell(3)
                        .setCellValue(
                                invoice.getCustomerState()
                        );

                row.createCell(4)
                        .setCellValue(
                                invoice.getTotalTaxable()
                                        .doubleValue()
                        );

                row.createCell(5)
                        .setCellValue(
                                invoice.getTotalCGST()
                                        .doubleValue()
                        );

                row.createCell(6)
                        .setCellValue(
                                invoice.getTotalSGST()
                                        .doubleValue()
                        );

                row.createCell(7)
                        .setCellValue(
                                invoice.getTotalIGST()
                                        .doubleValue()
                        );

                row.createCell(8)
                        .setCellValue(
                                invoice.getGrandTotal()
                                        .doubleValue()
                        );
            }

            // ================= AUTO SIZE =================

            for (int i = 0; i < 9; i++) {

                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(outputStream);

            workbook.close();

            return ResponseEntity.ok()

                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=gst-report.xlsx"
                    )

                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    )

                    .body(
                            outputStream.toByteArray()
                    );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate

    ) {

        try {

            List<Invoice> invoices =
                    invoiceRepository
                            .findByInvoiceDateBetween(
                                    fromDate,
                                    toDate
                            );

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(PageSize.A4.rotate());

            PdfWriter.getInstance(document, out);

            document.open();

            // ================= TITLE =================

            Font titleFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );

            Paragraph title =
                    new Paragraph(
                            "GST REPORT",
                            titleFont
                    );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            // ================= TABLE =================

            PdfPTable table =
                    new PdfPTable(9);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            3, 2, 3, 2,
                            2, 2, 2, 2, 2
                    }
            );

            // ================= HEADER =================

            addHeader(table, "Invoice No");

            addHeader(table, "Date");

            addHeader(table, "Customer");

            addHeader(table, "State");

            addHeader(table, "Taxable");

            addHeader(table, "CGST");

            addHeader(table, "SGST");

            addHeader(table, "IGST");

            addHeader(table, "Total");

            // ================= DATA =================

            for (Invoice invoice : invoices) {

                table.addCell(
                        invoice.getInvoiceNumber()
                );

                table.addCell(
                        invoice.getInvoiceDate()
                                .toString()
                );

                table.addCell(
                        invoice.getCustomerName()
                );

                table.addCell(
                        invoice.getCustomerState()
                );

                table.addCell(
                        invoice.getTotalTaxable()
                                .toString()
                );

                table.addCell(
                        invoice.getTotalCGST()
                                .toString()
                );

                table.addCell(
                        invoice.getTotalSGST()
                                .toString()
                );

                table.addCell(
                        invoice.getTotalIGST()
                                .toString()
                );

                table.addCell(
                        invoice.getGrandTotal()
                                .toString()
                );
            }

            document.add(table);

            document.close();

            return ResponseEntity.ok()

                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=gst-report.pdf"
                    )

                    .contentType(
                            MediaType.APPLICATION_PDF
                    )

                    .body(out.toByteArray());

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
    
    private void addHeader(
            PdfPTable table,
            String text
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(text)
                );

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        table.addCell(cell);
    }
}