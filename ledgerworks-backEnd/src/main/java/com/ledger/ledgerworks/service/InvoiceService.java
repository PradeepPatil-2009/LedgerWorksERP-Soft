package com.ledger.ledgerworks.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledgerworks.entity.DeliveryChallan;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.enums.DocumentType;
import com.ledger.ledgerworks.repository.DeliveryChallanRepository;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ledger.ledgerworks.service.GstCalculatorService;
import com.ledger.ledgerworks.entity.InvoiceItem;
import com.ledger.ledgerworks.entity.DeliveryChallanItem;

@Service
public class InvoiceService {

    private static final Logger log =
            LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private DeliveryChallanRepository deliveryChallanRepository;

    @Autowired
    private DocumentNumberService documentNumberService;

    @Autowired
    private NumberSeriesService numberSeriesService;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private GstCalculatorService gstCalculatorService;

    @Autowired
    private AccountingPostingService accountingPostingService;

    // GET ALL INVOICES

    public List<Invoice> getAll() {

        return invoiceRepository.findAll();
    }

    // CREATE MANUAL INVOICE

    @Transactional
    public Invoice create(
            Invoice invoice
    ) {

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(
                invoice.getInvoiceDate()
        );

        // ================= DOCUMENT NUMBER =================
        // Use admin-configured series; fall back to the legacy generator.

        if (invoice.getInvoiceNumber() == null
                || invoice.getInvoiceNumber().isBlank()) {

            invoice.setInvoiceNumber(
                    nextInvoiceNumber()
            );
        }

        // ================= GST AUTO CALCULATION =================

        gstCalculatorService.calculate(invoice);

        // ================= DEFAULT PAYMENT VALUES =================

        invoice.setPaidAmount(
                BigDecimal.ZERO
        );

        invoice.setOutstandingAmount(
                invoice.getGrandTotal()
        );

        invoice.setPaymentStatus(
                "PENDING"
        );

        invoice.setPaidStatus(
                "UNPAID"
        );

        invoice.setCreatedAt(
                LocalDateTime.now()
        );

        // ================= SAVE =================

        Invoice saved = invoiceRepository.save(
                invoice
        );

        // ================= LEDGER POSTING =================
        // Post the balanced sales journal on the invoice's own date, inside the
        // same transaction. Best-effort: never breaks invoice creation.

        accountingPostingService.postSalesInvoice(saved);

        return saved;
    }

    // CONVERT DELIVERY CHALLAN TO INVOICE

    public Invoice convertFromChallan(
            Long challanId
    ) {

        DeliveryChallan challan =
                deliveryChallanRepository
                .findById(challanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Challan not found"
                        ));

        // AVOID DUPLICATE INVOICE

        if (challan.getInvoice() != null) {

            return challan.getInvoice();
        }

        Invoice invoice =
                createFromChallan(challan);

        challan.setInvoice(invoice);

        deliveryChallanRepository.save(
                challan
        );

        return invoice;
    }

    // CREATE INVOICE FROM CHALLAN

    @Transactional
    public Invoice createFromChallan(
            DeliveryChallan challan
    ) {

        Invoice invoice =
                new Invoice();

        invoice.setInvoiceDate(
                LocalDate.now()
        );

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(
                invoice.getInvoiceDate()
        );

        // ================= DOCUMENT NUMBER =================
        // Use admin-configured series; fall back to the legacy generator.

        invoice.setInvoiceNumber(
                nextInvoiceNumber()
        );

        invoice.setDueDate(
                LocalDate.now().plusDays(30)
        );

        invoice.setCustomerName(
                challan.getCustomerName()
        );

        invoice.setCustomerAddress(
                challan.getCustomerAddress()
        );

        invoice.setCustomerPhone(
                challan.getCustomerPhone()
        );

        invoice.setCustomerEmail(
                challan.getCustomerEmail()
        );

        invoice.setCustomerState(
                challan.getPlaceOfSupply()
        );

        invoice.setDescriptions(
                challan.getDescriptions()
        );

        invoice.setTotalTaxable(
                challan.getTotalTaxable()
        );

        invoice.setTotalCGST(
                challan.getTotalCGST()
        );

        invoice.setTotalSGST(
                challan.getTotalSGST()
        );

        invoice.setTotalIGST(
                challan.getTotalIGST()
        );

        invoice.setGrandTotal(
                challan.getGrandTotal()
        );

        // IMPORTANT PAYMENT FIX

        invoice.setPaidAmount(
                BigDecimal.ZERO
        );

        invoice.setOutstandingAmount(
                challan.getGrandTotal()
        );

        invoice.setPaymentStatus(
                "PENDING"
        );

        invoice.setPaidStatus(
                "UNPAID"
        );

        invoice.setCreatedAt(
                LocalDateTime.now()
        );
     // ================= COPY DC ITEMS TO INVOICE =================

        for (DeliveryChallanItem dcItem : challan.getItems()) {

            InvoiceItem invoiceItem =
                    new InvoiceItem();

            invoiceItem.setInvoice(invoice);

            invoiceItem.setDescription(
                    dcItem.getDescription()
            );

            invoiceItem.setHsnCode(
                    dcItem.getHsnCode()
            );

            invoiceItem.setQuantity(
                    dcItem.getQuantity()
            );

            invoiceItem.setRate(
                    dcItem.getRate()
            );

            invoiceItem.setUnit(
                    dcItem.getUnit()
            );

            invoiceItem.setTaxableAmount(
                    dcItem.getTaxableAmount()
            );

            invoiceItem.setCgstAmount(
                    dcItem.getCgstAmount()
            );

            invoiceItem.setSgstAmount(
                    dcItem.getSgstAmount()
            );

            invoiceItem.setIgstAmount(
                    dcItem.getIgstAmount()
            );

            invoice.getItems().add(invoiceItem);
        }

        Invoice saved = invoiceRepository.save(
                invoice
        );

        // ================= LEDGER POSTING =================
        // Post the balanced sales journal on the invoice's own date, inside the
        // same transaction. Best-effort: never breaks invoice creation.

        accountingPostingService.postSalesInvoice(saved);

        return saved;
    }


 // GENERATE INVOICE PDF

    public byte[] generateInvoicePdf(
        Long invoiceId,
        String copyType
) {

    try {

        Invoice invoice =
                invoiceRepository.findById(invoiceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"
                                ));

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

        PdfWriter writer =
                PdfWriter.getInstance(
                        document,
                        out
                );

        document.open();

        // =========================
        // WATERMARK
        // =========================

        try {

            InputStream watermarkStream =
                    getClass()
                            .getResourceAsStream(
                                    "/static/page-1.png"
                            );

            if (watermarkStream != null) {

                byte[] logoBytes =
                        watermarkStream.readAllBytes();

                Image watermark =
                        Image.getInstance(
                                logoBytes
                        );

                watermark.scaleToFit(
                        250,
                        250
                );

                watermark.setAbsolutePosition(
                        180,
                        220
                );

                PdfGState gs =
                        new PdfGState();

                gs.setFillOpacity(0.08f);

                PdfContentByte canvas =
                        writer.getDirectContentUnder();

                canvas.saveState();

                canvas.setGState(gs);

                canvas.addImage(watermark);

                canvas.restoreState();
            }

        } catch (Exception e) {

            log.warn("Invoice watermark rendering failed", e);
        }

        // =========================
        // FONTS
        // =========================

        Font titleFont =
                new Font(
                        Font.HELVETICA,
                        18,
                        Font.BOLD
                );

        Font headerFont =
                new Font(
                        Font.HELVETICA,
                        10,
                        Font.BOLD
                );

        Font normalFont =
                new Font(
                        Font.HELVETICA,
                        9,
                        Font.NORMAL
                );

        Font smallFont =
                new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL
                );

        // =========================
        // COPY TYPE
        // =========================

        Paragraph copy =
                new Paragraph(
                        copyType + " COPY",
                        headerFont
                );

        document.add(copy);

        // =========================
        // TITLE
        // =========================

        Paragraph title =
                new Paragraph(
                        "TAX INVOICE",
                        titleFont
                );

        title.setAlignment(
                Element.ALIGN_CENTER
        );

        title.setSpacingAfter(10f);

        document.add(title);

        // =========================
        // COMPANY TABLE
        // =========================

        PdfPTable companyTable =
                new PdfPTable(2);

        companyTable.setWidthPercentage(100);

        companyTable.setWidths(
                new float[]{15, 85}
        );

        PdfPCell logoCell;

        try {

            InputStream logoStream =
                    getClass()
                            .getResourceAsStream(
                                    "/static/page-1.png"
                            );

            if (logoStream != null) {

                byte[] logoBytes =
                        logoStream.readAllBytes();

                Image logo =
                        Image.getInstance(
                                logoBytes
                        );

                logo.scaleToFit(60, 60);

                logoCell =
                        new PdfPCell(
                                logo,
                                true
                        );

            } else {

                logoCell =
                        new PdfPCell(
                                new Phrase(
                                        "LOGO",
                                        headerFont
                                )
                        );
            }

        } catch (Exception e) {

            logoCell =
                    new PdfPCell(
                            new Phrase(
                                    "LOGO",
                                    headerFont
                            )
                    );
        }

        logoCell.setFixedHeight(80f);

        logoCell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        logoCell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        companyTable.addCell(logoCell);

        PdfPCell companyInfo =
                new PdfPCell();

        companyInfo.addElement(
                new Paragraph(
                        "SANDHYA ENGINEERING",
                        headerFont
                )
        );

        companyInfo.addElement(
                new Paragraph(
                        "Talawade, Pune - 411062",
                        normalFont
                )
        );

        companyInfo.addElement(
                new Paragraph(
                        "Phone : 8766763568",
                        normalFont
                )
        );

        companyInfo.addElement(
                new Paragraph(
                        "GSTIN : 27BUIPJ1964R1ZU",
                        normalFont
                )
        );

        companyInfo.addElement(
                new Paragraph(
                        "Email : sandhyaengineering1988@gmail.com",
                        normalFont
                )
        );

        companyTable.addCell(companyInfo);

        document.add(companyTable);

        // =========================
        // BILL + INVOICE DETAILS
        // =========================

        PdfPTable detailsTable =
                new PdfPTable(2);

        detailsTable.setWidthPercentage(100);

        detailsTable.setSpacingBefore(8f);

        detailsTable.setWidths(
                new float[]{50, 50}
        );

        PdfPCell billCell =
                new PdfPCell();

        billCell.addElement(
                new Paragraph(
                        "Bill To:",
                        headerFont
                )
        );

        billCell.addElement(
                new Paragraph(
                        invoice.getCustomerName(),
                        headerFont
                )
        );

        billCell.addElement(
                new Paragraph(
                        invoice.getCustomerAddress(),
                        normalFont
                )
        );

        billCell.addElement(
                new Paragraph(
                        "Phone : "
                                + invoice.getCustomerPhone(),
                        normalFont
                )
        );

        billCell.addElement(
                new Paragraph(
                        "Email : "
                                + invoice.getCustomerEmail(),
                        normalFont
                )
        );

        detailsTable.addCell(billCell);

        PdfPCell invoiceCell =
                new PdfPCell();

        invoiceCell.addElement(
                new Paragraph(
                        "Invoice Details:",
                        headerFont
                )
        );

        invoiceCell.addElement(
                new Paragraph(
                        "Invoice No : "
                                + invoice.getInvoiceNumber(),
                        normalFont
                )
        );

        invoiceCell.addElement(
                new Paragraph(
                        "Date : "
                                + invoice.getInvoiceDate(),
                        normalFont
                )
        );
        invoiceCell.addElement(
                new Paragraph(
                        "Place Of Supply : "
                                + safeStr(invoice.getCustomerState()),
                        normalFont
                )
        );
        invoiceCell.addElement(
                new Paragraph(
                        "Copy Type : "
                                + copyType,
                        normalFont
                )
        );

        detailsTable.addCell(invoiceCell);

        document.add(detailsTable);

        // =========================
        // ITEM TABLE
        // =========================

        PdfPTable itemTable =
                new PdfPTable(7);

        itemTable.setWidthPercentage(100);

        itemTable.setSpacingBefore(10f);

        itemTable.setWidths(
                new float[]{
                        5,
                        25,
                        15,
                        12,
                        13,
                        15,
                     //   10,
                        15
                }
        );

        String[] headers = {
                "#",
                "Item Name",
                "HSN/SAC",
                "Qty",
                "Unit",
                "Price",
              //  "GST",
                "Amount"
        };

        for (String h : headers) {

            PdfPCell cell =
                    new PdfPCell(
                            new Phrase(
                                    h,
                                    headerFont
                            )
                    );

            cell.setBackgroundColor(
                    Color.LIGHT_GRAY
            );

            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            itemTable.addCell(cell);
        }

        if (invoice.getItems() != null) {

            int srNo = 1;

            for (InvoiceItem item : invoice.getItems()) {

                itemTable.addCell(
                        new Phrase(
                                String.valueOf(srNo++),
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                safeStr(item.getDescription()),
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                safeStr(item.getHsnCode()),
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                item.getQuantity() != null
                                        ? item.getQuantity().toString()
                                        : "0",
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                safeStr(item.getUnit()),
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                item.getRate() != null
                                        ? item.getRate().toString()
                                        : "0.00",
                                normalFont
                        )
                );

                itemTable.addCell(
                        new Phrase(
                                item.getTaxableAmount() != null
                                        ? item.getTaxableAmount().toString()
                                        : "0.00",
                                normalFont
                        )
                );
            }
        }
       /* itemTable.addCell(
                new Phrase(
                        invoice.getTotalCGST().toString(),
                        normalFont
                )
        );*/

        

        document.add(itemTable);

        // =========================
        // TAX + TOTAL SECTION
        // =========================

        PdfPTable taxTable =
                new PdfPTable(2);

        taxTable.setWidthPercentage(100);

        taxTable.setSpacingBefore(5f);

        taxTable.setWidths(
                new float[]{65, 35}
        );

        // LEFT TAX SUMMARY

        PdfPTable taxSummary =
                new PdfPTable(5);

        taxSummary.setWidthPercentage(100);

        String[] taxHeaders = {
                "Taxable",
                "CGST",
                "SGST",
                "IGST",
                "Total"
        };

        for (String h : taxHeaders) {

            PdfPCell cell =
                    new PdfPCell(
                            new Phrase(
                                    h,
                                    headerFont
                            )
                    );

            cell.setBackgroundColor(
                    Color.LIGHT_GRAY
            );

            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            taxSummary.addCell(cell);
        }

        taxSummary.addCell(
                new Phrase(
                        invoice.getTotalTaxable().toString(),
                        normalFont
                )
        );

        taxSummary.addCell(
                new Phrase(
                        invoice.getTotalCGST().toString(),
                        normalFont
                )
        );

        taxSummary.addCell(
                new Phrase(
                        invoice.getTotalSGST().toString(),
                        normalFont
                )
        );
        taxSummary.addCell(
                new Phrase(
                        invoice.getTotalIGST().toString(),
                        normalFont
                )
        );

        BigDecimal roundedGrandTotal =
                invoice.getGrandTotal()
                        .setScale(0, java.math.RoundingMode.HALF_UP);

        taxSummary.addCell(
                new Phrase(
                        roundedGrandTotal.toString(),
                        normalFont
                )
        );

        PdfPCell taxLeft =
                new PdfPCell(
                        taxSummary
                );

        taxTable.addCell(taxLeft);

        // RIGHT TOTALS

        PdfPTable totalTable =
                new PdfPTable(2);

        totalTable.setWidthPercentage(100);

        totalTable.addCell(
                new Phrase(
                        "SubTotal",
                        headerFont
                )
        );

        totalTable.addCell(
                new Phrase(
                        invoice.getTotalTaxable().toString(),
                        normalFont
                )
        );

        totalTable.addCell(
                new Phrase(
                        "Grand Total",
                        headerFont
                )
        );

        totalTable.addCell(
                new Phrase(
                		roundedGrandTotal.toString(),
                        headerFont
                )
        );

        totalTable.addCell(
                new Phrase(
                        "Received",
                        headerFont
                )
        );

        totalTable.addCell(
                new Phrase(
                        "0.00",
                        normalFont
                )
        );

        totalTable.addCell(
                new Phrase(
                        "Balance",
                        headerFont
                )
        );

        totalTable.addCell(
                new Phrase(
                		roundedGrandTotal.toString(),
                        normalFont
                )
        );

        PdfPCell totalRight =
                new PdfPCell(
                        totalTable
                );

        taxTable.addCell(totalRight);

        document.add(taxTable);

        // =========================
        // AMOUNT IN WORDS
        // =========================

        

        Paragraph words =
                new Paragraph(
                        "Invoice Amount In Words : "
                                + convertAmountToWords(
                                        roundedGrandTotal.intValue()
                                )
                                + " Only",
                        normalFont
                );

        words.setSpacingBefore(8f);

        document.add(words);

        // =========================
        // DESCRIPTION + TERMS
        // =========================

        PdfPTable footerTable =
                new PdfPTable(2);

        footerTable.setWidthPercentage(100);

        footerTable.setSpacingBefore(10f);

        footerTable.setWidths(
                new float[]{50, 50}
        );

        PdfPCell descCell =
                new PdfPCell();

        descCell.addElement(
                new Paragraph(
                        "Description:",
                        headerFont
                )
        );

        descCell.addElement(
                new Paragraph(
                        "Annexure attached herewith",
                        smallFont
                )
        );

        footerTable.addCell(descCell);

        PdfPCell termsCell =
                new PdfPCell();

        termsCell.addElement(
                new Paragraph(
                        "Terms And Conditions:",
                        headerFont
                )
        );

        termsCell.addElement(
                new Paragraph(
                        "Thank you for doing business with us.",
                        smallFont
                )
        );

        termsCell.addElement(
                new Paragraph(
                        "\nFor SANDHYA ENGINEERING:",
                        headerFont
                )
        );

        termsCell.addElement(
                new Paragraph(
                        "\n\nAuthorized Signature",
                        smallFont
                )
        );

        footerTable.addCell(termsCell);

        document.add(footerTable);

        document.close();

        return out.toByteArray();

    } catch (Exception e) {

        log.error("Invoice PDF generation failed", e);

        throw new RuntimeException(
                "Invoice PDF generation failed"
        );
    }
}
    
    // ================= NEXT INVOICE NUMBER =================
    // Prefer the admin-configured NumberSeries; if it has no row or
    // throws, fall back to the legacy DocumentNumberService so that
    // invoice creation never fails because of numbering.

    private String nextInvoiceNumber() {

        try {

            String number =
                    numberSeriesService.next(DocumentType.INVOICE);

            if (number != null && !number.isBlank()) {

                return number;
            }

        } catch (Exception e) {

            log.warn(
                    "NumberSeries lookup failed for INVOICE; "
                            + "falling back to legacy numbering",
                    e
            );
        }

        Invoice last =
                invoiceRepository.findTopByOrderByIdDesc();

        return documentNumberService.generateNumber(
                "INV",
                last != null ? last.getInvoiceNumber() : null
        );
    }

    // Helper method
    private String safeStr(String val) {

        return val != null
                ? val
                : "";
    }

    private PdfPCell headerCell(String text) {

        PdfPCell cell = new PdfPCell(
                new Phrase(text)
        );

        cell.setBackgroundColor(Color.LIGHT_GRAY);

        return cell;
    }

    private PdfPCell bodyCell(String text) {

        return new PdfPCell(
                new Phrase(text)
        );
    }

    private PdfPCell signatureBox(String title) {

        PdfPCell cell = new PdfPCell();

        cell.setFixedHeight(100);

        cell.addElement(
                new Paragraph(
                        title,
                        new Font(
                                Font.HELVETICA,
                                10,
                                Font.BOLD
                        )
                )
        );

        cell.addElement(
                new Paragraph("\n\nName:")
        );

        cell.addElement(
                new Paragraph("\nSignature:")
        );

        return cell;
    }
    private String convertAmountToWords(int number) {
    	if (number == 0) {
            return "Zero";
        }	
        String[] units = {
                "",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
                "Eight",
                "Nine",
                "Ten",
                "Eleven",
                "Twelve",
                "Thirteen",
                "Fourteen",
                "Fifteen",
                "Sixteen",
                "Seventeen",
                "Eighteen",
                "Nineteen"
        };

        String[] tens = {
                "",
                "",
                "Twenty",
                "Thirty",
                "Forty",
                "Fifty",
                "Sixty",
                "Seventy",
                "Eighty",
                "Ninety"
        };

        if (number < 20) {
            return units[number] + " Rupees";
        }

        if (number < 100) {
            return tens[number / 10] + " "
                    + units[number % 10]
                    + " Rupees";
        }

        if (number < 1000) {
            return units[number / 100]
                    + " Hundred "
                    + convertAmountToWords(number % 100);
        }

        if (number < 100000) {
            return convertAmountToWords(number / 1000)
                    + " Thousand "
                    + convertAmountToWords(number % 1000);
        }

        if (number < 10000000) {
            return convertAmountToWords(number / 100000)
                    + " Lakh "
                    + convertAmountToWords(number % 100000);
        }

        return String.valueOf(number);
    }
}