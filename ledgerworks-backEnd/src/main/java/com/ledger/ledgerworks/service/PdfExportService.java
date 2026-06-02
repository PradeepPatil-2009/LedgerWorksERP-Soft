package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.TrialBalanceResponse;
import com.ledger.ledgerworks.dto.TrialBalanceRow;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PdfExportService {

    private final TrialBalanceService trialBalanceService;

    public PdfExportService(
            TrialBalanceService trialBalanceService
    ) {

        this.trialBalanceService =
                trialBalanceService;
    }

    public byte[] generateTrialBalancePdf(

            LocalDate fromDate,
            LocalDate toDate

    ) {

        try {

            TrialBalanceResponse response =
                    trialBalanceService
                            .generateTrialBalance(
                                    fromDate,
                                    toDate
                            );

            List<TrialBalanceRow> rows =
                    response.getRows();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            20,
                            20,
                            20,
                            20
                    );

            PdfWriter.getInstance(
                    document,
                    out
            );

            document.open();

            // ===============================
            // FONTS
            // ===============================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11
                    );

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10
                    );

            // ===============================
            // COMPANY NAME
            // ===============================

            Paragraph company =
                    new Paragraph(
                            "LEDGERWORKS ERP",
                            titleFont
                    );

            company.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(company);

            // ===============================
            // REPORT TITLE
            // ===============================

            Paragraph title =
                    new Paragraph(
                            "TRIAL BALANCE REPORT",
                            headerFont
                    );

            title.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(title);

            // ===============================
            // DATE RANGE
            // ===============================

            Paragraph date =
                    new Paragraph(
                            "From : " +
                                    fromDate +
                                    "    To : " +
                                    toDate,
                            normalFont
                    );

            date.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(date);

            document.add(
                    new Paragraph(" ")
            );

            // ===============================
            // TABLE
            // ===============================

            PdfPTable table =
                    new PdfPTable(3);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            5f,
                            2f,
                            2f
                    }
            );

            // ===============================
            // HEADER CELLS
            // ===============================

            addHeaderCell(
                    table,
                    "Account"
            );

            addHeaderCell(
                    table,
                    "Debit"
            );

            addHeaderCell(
                    table,
                    "Credit"
            );

            // ===============================
            // DATA
            // ===============================

            BigDecimal totalDebit =
                    BigDecimal.ZERO;

            BigDecimal totalCredit =
                    BigDecimal.ZERO;

            for (TrialBalanceRow row : rows) {

                BigDecimal debit =
                        row.getDebit() == null
                                ? BigDecimal.ZERO
                                : row.getDebit();

                BigDecimal credit =
                        row.getCredit() == null
                                ? BigDecimal.ZERO
                                : row.getCredit();

                table.addCell(
                        new Phrase(
                                row.getAccountName(),
                                normalFont
                        )
                );

                table.addCell(
                        new Phrase(
                                debit.toString(),
                                normalFont
                        )
                );

                table.addCell(
                        new Phrase(
                                credit.toString(),
                                normalFont
                        )
                );

                totalDebit =
                        totalDebit.add(debit);

                totalCredit =
                        totalCredit.add(credit);
            }

            // ===============================
            // TOTAL ROW
            // ===============================

            PdfPCell totalCell =
                    new PdfPCell(
                            new Phrase(
                                    "TOTAL",
                                    boldFont
                            )
                    );

            totalCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            table.addCell(totalCell);

            table.addCell(
                    new Phrase(
                            totalDebit.toString(),
                            boldFont
                    )
            );

            table.addCell(
                    new Phrase(
                            totalCredit.toString(),
                            boldFont
                    )
            );

            document.add(table);

            document.add(
                    new Paragraph(" ")
            );

            // ===============================
            // FOOTER
            // ===============================

            Paragraph footer =
                    new Paragraph(
                            "This is a computer generated report.",
                            normalFont
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(footer);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generating PDF",
                    e
            );
        }
    }

    // ==========================================
    // HELPER METHOD
    // ==========================================

    private void addHeaderCell(

            PdfPTable table,
            String text

    ) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10
                );

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text,
                                font
                        )
                );

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setBackgroundColor(
                Color.LIGHT_GRAY
        );

        table.addCell(cell);
    }
}