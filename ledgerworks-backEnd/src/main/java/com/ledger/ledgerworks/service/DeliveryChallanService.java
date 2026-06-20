
package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.*;
import com.ledger.ledgerworks.enums.DocumentType;
import com.ledger.ledgerworks.repository.DeliveryChallanRepository;

import java.util.Map;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryChallanService {

    private static final Logger log =
            LoggerFactory.getLogger(DeliveryChallanService.class);

    @Autowired
    private DeliveryChallanRepository repo;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private DocumentNumberService documentNumberService;

    @Autowired
    private NumberSeriesService numberSeriesService;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private GstUtilityService gstUtilityService;

    // Compact state-name -> GST code lookup for when a place-of-supply is
    // supplied as a NAME (or the legacy "MH") rather than a GSTIN.
    private static final Map<String, String> NAME_TO_CODE = Map.ofEntries(
            Map.entry("jammu and kashmir", "01"),
            Map.entry("himachal pradesh", "02"),
            Map.entry("punjab", "03"),
            Map.entry("chandigarh", "04"),
            Map.entry("uttarakhand", "05"),
            Map.entry("haryana", "06"),
            Map.entry("delhi", "07"),
            Map.entry("rajasthan", "08"),
            Map.entry("uttar pradesh", "09"),
            Map.entry("bihar", "10"),
            Map.entry("sikkim", "11"),
            Map.entry("arunachal pradesh", "12"),
            Map.entry("nagaland", "13"),
            Map.entry("manipur", "14"),
            Map.entry("mizoram", "15"),
            Map.entry("tripura", "16"),
            Map.entry("meghalaya", "17"),
            Map.entry("assam", "18"),
            Map.entry("west bengal", "19"),
            Map.entry("jharkhand", "20"),
            Map.entry("odisha", "21"),
            Map.entry("chhattisgarh", "22"),
            Map.entry("madhya pradesh", "23"),
            Map.entry("gujarat", "24"),
            Map.entry("daman and diu", "25"),
            Map.entry("dadra and nagar haveli", "26"),
            Map.entry("maharashtra", "27"),
            Map.entry("mh", "27"),
            Map.entry("karnataka", "29"),
            Map.entry("goa", "30"),
            Map.entry("lakshadweep", "31"),
            Map.entry("kerala", "32"),
            Map.entry("tamil nadu", "33"),
            Map.entry("puducherry", "34"),
            Map.entry("andaman and nicobar islands", "35"),
            Map.entry("telangana", "36"),
            Map.entry("andhra pradesh", "37"),
            Map.entry("ladakh", "38")
    );

    // ================= CREATE =================

    public DeliveryChallan create(DeliveryChallan c) {

        if (c.getItems() == null || c.getItems().isEmpty()) {

            throw new RuntimeException("Items required");
        }

        c.setChallanDate(LocalDate.now());

        // ================= FINANCIAL YEAR LOCK CHECK =================

        financialYearService.assertOpen(
                c.getChallanDate()
        );

        // ================= DOCUMENT NUMBER =================
        // Use admin-configured series; fall back to the legacy generator.

        c.setChallanNumber(
                nextChallanNumber()
        );

        c.setStatus("ACTIVE");

        for (DeliveryChallanItem i : c.getItems()) {

            i.setChallan(c);
        }

        calculate(c);

        return repo.save(c);
    }

    // ================= UPDATE =================

    public DeliveryChallan update(Long id, DeliveryChallan updated) {

        DeliveryChallan existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!"ACTIVE".equals(existing.getStatus())) {

            throw new RuntimeException("Only ACTIVE editable");
        }

        existing.setCustomerName(updated.getCustomerName());

        existing.setCustomerGST(updated.getCustomerGST());

        existing.setPlaceOfSupply(updated.getPlaceOfSupply());

        existing.setCustomerAddress(updated.getCustomerAddress());

        existing.setCustomerEmail(updated.getCustomerEmail());

        existing.setCustomerPhone(updated.getCustomerPhone());

        existing.setTransportName(updated.getTransportName());

        existing.setVehicleNumber(updated.getVehicleNumber());

        existing.setDescriptions(updated.getDescriptions());

        existing.getItems().clear();

        for (DeliveryChallanItem i : updated.getItems()) {

            i.setChallan(existing);

            existing.getItems().add(i);
        }

        calculate(existing);

        return repo.save(existing);
    }

    // ================= CANCEL =================

    public DeliveryChallan cancel(Long id) {

        DeliveryChallan c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if ("DELIVERED".equals(c.getStatus())) {

            throw new RuntimeException(
                    "Delivered challan cannot cancel"
            );
        }

        c.setStatus("CANCELLED");

        return repo.save(c);
    }

    // ================= DELIVER =================

    public DeliveryChallan deliver(Long id) {

        try {

            log.debug("Deliver started for challan id {}", id);

            DeliveryChallan dc =
                    repo.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Delivery Challan not found"
                                    )
                            );

            if ("DELIVERED".equalsIgnoreCase(dc.getStatus())) {

                throw new RuntimeException(
                        "Already delivered"
                );
            }

            dc.setStatus("DELIVERED");

            if (dc.getInvoice() == null) {

                Invoice invoice =
                        invoiceService.createFromChallan(dc);

                dc.setInvoice(invoice);

                dc.setInvoiceCreated(true);
            }

            DeliveryChallan saved =
                    repo.save(dc);

            log.debug("Deliver completed for challan id {}", id);

            return saved;

        } catch (Exception e) {

            log.error("Deliver failed for challan id {}", id, e);

            throw new RuntimeException(
                    e.getMessage()
            );
        }
    }

    // ================= GET ALL =================

    public List<DeliveryChallan> getAll() {

        return repo.findAll();
    }

    // ================= GST CALCULATION =================

    private void calculate(DeliveryChallan c) {

        BigDecimal totalTaxable = BigDecimal.ZERO;

        BigDecimal totalCGST = BigDecimal.ZERO;

        BigDecimal totalSGST = BigDecimal.ZERO;

        BigDecimal totalIGST = BigDecimal.ZERO;

        boolean isIntraState =
                isIntraState(c);

        for (DeliveryChallanItem i : c.getItems()) {

            BigDecimal qty = safe(i.getQuantity());

            BigDecimal rate = safe(i.getRate());

            BigDecimal taxable = qty.multiply(rate);

            BigDecimal cgstPercent =
                    safe(i.getCgstPercent());

            BigDecimal sgstPercent =
                    safe(i.getSgstPercent());

            BigDecimal igstPercent =
                    safe(i.getIgstPercent());

            BigDecimal cgst = BigDecimal.ZERO;

            BigDecimal sgst = BigDecimal.ZERO;

            BigDecimal igst = BigDecimal.ZERO;

            if (isIntraState) {

                cgst = taxable.multiply(cgstPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

                sgst = taxable.multiply(sgstPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

            } else {

                igst = taxable.multiply(igstPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );
            }

            BigDecimal total =
                    taxable
                            .add(cgst)
                            .add(sgst)
                            .add(igst);

            i.setTaxableAmount(taxable);

            i.setCgstAmount(cgst);

            i.setSgstAmount(sgst);

            i.setIgstAmount(igst);

            i.setTotalAmount(total);

            totalTaxable =
                    totalTaxable.add(taxable);

            totalCGST =
                    totalCGST.add(cgst);

            totalSGST =
                    totalSGST.add(sgst);

            totalIGST =
                    totalIGST.add(igst);
        }

        c.setTotalTaxable(totalTaxable);

        c.setTotalCGST(totalCGST);

        c.setTotalSGST(totalSGST);

        c.setTotalIGST(totalIGST);

        c.setGrandTotal(

                totalTaxable
                        .add(totalCGST)
                        .add(totalSGST)
                        .add(totalIGST)
        );
    }

    private BigDecimal safe(BigDecimal val) {

        return val != null
                ? val
                : BigDecimal.ZERO;
    }

    // ================= STATE / GST HEAD RESOLUTION =================
    // Mirror the invoice foundation so a challan and the invoice converted from
    // it never disagree on tax heads: derive the company's own state code from
    // its configured GSTIN and compare it to the customer's place-of-supply
    // code. CGST + SGST when both codes are known AND equal (intra-state); IGST
    // otherwise (inter-state, or when either side's state cannot be resolved).

    private boolean isIntraState(DeliveryChallan c) {

        String companyCode = resolveCompanyStateCode();
        String customerCode = resolveCustomerStateCode(c);

        if (companyCode != null && customerCode != null) {
            return companyCode.equals(customerCode);
        }

        // Legacy fallback (no company GSTIN configured): mirror
        // GstCalculatorService.legacyIntraStateFallback so a challan and the
        // invoice converted from it NEVER disagree on the tax heads. Without a
        // configured company state we cannot trust a code comparison, so treat a
        // Maharashtra place-of-supply (the previous default company state) as
        // intra-state; anything else (including blank) is inter-state.
        String place = c.getPlaceOfSupply();
        return place != null && place.trim().equalsIgnoreCase("Maharashtra");
    }

    // Company's own state code from the configured GSTIN.
    private String resolveCompanyStateCode() {

        try {

            CompanySettings settings =
                    companySettingsService.getSettings();

            if (settings == null) {
                return null;
            }

            return gstUtilityService.codeFromGst(
                    settings.getGstNumber()
            );

        } catch (Exception ex) {
            return null;
        }
    }

    // Customer place-of-supply code: prefer the customer GSTIN, then a
    // GSTIN-like place-of-supply, then a state name (including the legacy "MH").
    private String resolveCustomerStateCode(DeliveryChallan c) {

        String fromGst =
                gstUtilityService.codeFromGst(c.getCustomerGST());

        if (fromGst != null) {
            return fromGst;
        }

        String place = c.getPlaceOfSupply();

        if (place == null) {
            return null;
        }

        String trimmed = place.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        // A GSTIN-like place of supply (starts with 2 digits).
        if (trimmed.length() >= 2
                && Character.isDigit(trimmed.charAt(0))
                && Character.isDigit(trimmed.charAt(1))) {

            return trimmed.substring(0, 2);
        }

        return NAME_TO_CODE.get(trimmed.toLowerCase());
    }

    // ================= PDF =================

   /* public byte[] generateChallanPdf(Long id)
            throws Exception {
    			DeliveryChallan c = repo.findById(id).orElseThrow(() -> new RuntimeException("Delivery Challan Not Found"));

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document doc = new Document(PageSize.A4, 20, 20, 20, 20);

            PdfWriter.getInstance(doc, out);

            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(25, 45, 120));

            Font bold = new Font(Font.HELVETICA, 10, Font.BOLD);

            Font normal = new Font(Font.HELVETICA, 9, Font.NORMAL);

            Font small = new Font(Font.HELVETICA, 8, Font.NORMAL);

            Paragraph title = new Paragraph("DELIVERY CHALLAN", titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            title.setSpacingAfter(12);

            doc.add(title);

            PdfPTable company = new PdfPTable(new float[]{1, 4});

            company.setWidthPercentage(100);

			
			 * PdfPCell logo = new PdfPCell(new Phrase("LOGO", bold));
			 * 
			 * logo.setFixedHeight(80);
			 * 
			 * logo.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * 
			 * logo.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * 
			 * company.addCell(logo);
			 
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

                    logo.scaleToFit(55, 55);

                    logo.setAlignment(
                            Element.ALIGN_CENTER
                    );

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

            logoCell.setFixedHeight(65);

            logoCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            logoCell.setVerticalAlignment(
                    Element.ALIGN_MIDDLE
            );

            

            company.addCell(logoCell);

            PdfPCell comp = new PdfPCell();

            comp.addElement(new Paragraph("SANDHYA ENGINEERING", bold));

            comp.addElement(new Paragraph(
                    "Gat no-255/5, Jyotibanagar,\nTalawade, Pune - 411062",
                    normal));

            comp.addElement(new Paragraph(
                    "Phone : 8766763568",
                    normal));

            comp.addElement(new Paragraph(
                    "GSTIN : 27BUIPJ1964R1ZU",
                    bold));

            comp.addElement(new Paragraph(
                    "Email : sandhyaengineering1988@gmail.com",
                    normal));

            company.addCell(comp);

            doc.add(company);

            doc.add(new Paragraph(" "));

            PdfPTable info = new PdfPTable(2);

            info.setWidthPercentage(100);

            info.setSpacingBefore(5);

            PdfPCell customer = new PdfPCell();

            customer.addElement(new Paragraph("Delivery Challan For", bold));

            customer.addElement(new Paragraph(
                    safeStr(c.getCustomerName()),
                    bold));

            customer.addElement(new Paragraph(
                    safeStr(c.getCustomerAddress()),
                    normal));

            customer.addElement(new Paragraph(
                    "GST : " + safeStr(c.getCustomerGST()),
                    normal));

            customer.addElement(new Paragraph(
                    "Phone : " + safeStr(c.getCustomerPhone()),
                    normal));

            customer.addElement(new Paragraph(
                    "Email : " + safeStr(c.getCustomerEmail()),
                    small));

            info.addCell(customer);

            PdfPCell details = new PdfPCell();

            details.addElement(new Paragraph("Challan Details", bold));

            details.addElement(new Paragraph(
                    "Challan No : " + safeStr(c.getChallanNumber()),
                    normal));

            details.addElement(new Paragraph(
                    "Date : " + safeStr(String.valueOf(c.getChallanDate())),
                    normal));

            details.addElement(new Paragraph(
                    "Vehicle No : " + safeStr(c.getVehicleNumber()),
                    normal));

            details.addElement(new Paragraph(
                    "Transport : " + safeStr(c.getTransportName()),
                    normal));

            details.addElement(new Paragraph(
                    "Place Of Supply : " + safeStr(c.getPlaceOfSupply()),
                    normal));

            info.addCell(details);

            doc.add(info);

            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(
                    new float[]{1, 4, 2, 2, 2, 2, 2});

            table.setWidthPercentage(100);

            table.setSpacingBefore(10);

            table.addCell(headerCell("#"));

            table.addCell(headerCell("Item"));

            table.addCell(headerCell("HSN"));

            table.addCell(headerCell("Qty"));

            table.addCell(headerCell("Rate"));

            table.addCell(headerCell("GST"));

            table.addCell(headerCell("Amount"));

            int sr = 1;

            BigDecimal totalQty = BigDecimal.ZERO;

            for (DeliveryChallanItem item : c.getItems()) {

                BigDecimal qty = safe(item.getQuantity());

                BigDecimal rate = safe(item.getRate());

                BigDecimal amount = safe(item.getTotalAmount());

                BigDecimal gst =
                        safe(item.getCgstAmount())
                                .add(safe(item.getSgstAmount()))
                                .add(safe(item.getIgstAmount()));

                table.addCell(bodyCell(String.valueOf(sr++)));

                table.addCell(bodyCell(
                        safeStr(item.getDescription())));

                table.addCell(bodyCell(
                        safeStr(item.getHsnCode())));

                table.addCell(bodyCell(
                        qty.setScale(2, RoundingMode.HALF_UP).toString()));

                table.addCell(bodyCell(
                        rate.setScale(2, RoundingMode.HALF_UP).toString()));

                table.addCell(bodyCell(
                        gst.setScale(2, RoundingMode.HALF_UP).toString()));

                table.addCell(bodyCell(
                        amount.setScale(2, RoundingMode.HALF_UP).toString()));

                totalQty = totalQty.add(qty);
            }

            PdfPCell totalLabel = new PdfPCell(
                    new Phrase("Total Quantity", bold));

            totalLabel.setColspan(3);

            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel);

            table.addCell(bodyCell(
                    totalQty.setScale(2, RoundingMode.HALF_UP).toString()));

            table.addCell(bodyCell(""));

            table.addCell(bodyCell(""));

            table.addCell(bodyCell(""));

            doc.add(table);

            doc.add(new Paragraph(" "));

            PdfPTable totals = new PdfPTable(2);

            totals.setWidthPercentage(35);

            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totals.addCell(headerCell("Taxable"));

            totals.addCell(bodyCell(
                    safe(c.getTotalTaxable())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()));

            totals.addCell(headerCell("CGST"));

            totals.addCell(bodyCell(
                    safe(c.getTotalCGST())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()));

            totals.addCell(headerCell("SGST"));

            totals.addCell(bodyCell(
                    safe(c.getTotalSGST())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()));

            totals.addCell(headerCell("IGST"));

            totals.addCell(bodyCell(
                    safe(c.getTotalIGST())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()));

            totals.addCell(headerCell("Grand Total"));

            totals.addCell(bodyCell(
                    safe(c.getGrandTotal())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()));

            doc.add(totals);

            doc.add(new Paragraph(" "));

            PdfPTable descTable = new PdfPTable(2);

            descTable.setWidthPercentage(100);

            PdfPCell desc = new PdfPCell();

            desc.addElement(new Paragraph("Comments", bold));

            desc.addElement(new Paragraph(
                    safeStr(c.getDescriptions()),
                    normal));

            descTable.addCell(desc);

            PdfPCell terms = new PdfPCell();

            terms.addElement(new Paragraph(
                    "Terms And Conditions",
                    bold));

            terms.addElement(new Paragraph(
                    "Thank you for doing business with us.",
                    normal));

            terms.addElement(new Paragraph(
                    "Goods once sold will not be taken back.",
                    small));

            descTable.addCell(terms);

            doc.add(descTable);

          //  doc.add(new Paragraph(" "));

            PdfPTable sign = new PdfPTable(3);

            sign.setWidthPercentage(100);

            sign.setSpacingBefore(30);

            sign.addCell(signatureBox("Received By"));

            sign.addCell(signatureBox("Delivered By"));

            sign.addCell(signatureBox("For SANDHYA ENGINEERING"));

            doc.add(sign);

            doc.close();

            return out.toByteArray();} */
    
    
    
    public byte[] generateChallanPdf(Long id) throws Exception {

        DeliveryChallan challan = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Challan not found"));

        Document document = new Document(PageSize.A4);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);

        document.open();
        
        try {

            InputStream watermarkStream =
                    getClass()
                            .getResourceAsStream(
                                    "/static/page-1.png"
                            );
            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            out
                    );

            document.open();
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

            log.warn("Delivery challan watermark rendering failed", e);
        }

        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                20
        );

        Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                11
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                10
        );

        // ================= TITLE =================

        Paragraph title = new Paragraph(
                "DELIVERY CHALLAN",
                titleFont
        );

        title.setAlignment(Element.ALIGN_CENTER);

        title.setSpacingAfter(15f);

        document.add(title);

        // ================= COMPANY TABLE =================

        PdfPTable companyTable = new PdfPTable(2);

        companyTable.setWidthPercentage(100);

        companyTable.setWidths(new float[]{1f, 4f});

        // LOGO

        PdfPCell logoCell;

        try {

            InputStream is = getClass()
                    .getResourceAsStream("/static/page-1.png");

            Image logo = Image.getInstance(is.readAllBytes());

            logo.scaleToFit(70, 70);

            logoCell = new PdfPCell(logo, false);

        } catch (Exception e) {

            logoCell = new PdfPCell(
                    new Phrase("LOGO")
            );
        }

        logoCell.setPadding(10);

        companyTable.addCell(logoCell);

        // COMPANY DETAILS

        PdfPCell companyCell = new PdfPCell();

        companyCell.addElement(new Paragraph(
                "SANDHYA ENGINEERING",
                headerFont
        ));

        companyCell.addElement(new Paragraph(
                "Gat no-255/5, Jyotibanagar,",
                normalFont
        ));

        companyCell.addElement(new Paragraph(
                "Talawade, Pune - 411062",
                normalFont
        ));

        companyCell.addElement(new Paragraph(
                "Phone : 8766763568",
                normalFont
        ));

        companyCell.addElement(new Paragraph(
                "GSTIN : 27BUIPJ1964R1ZU",
                headerFont
        ));

        companyCell.addElement(new Paragraph(
                "Email : sandhyaengineering1988@gmail.com",
                normalFont
        ));

        companyCell.setPaddingTop(12f);

        companyCell.setPaddingBottom(12f);

        companyCell.setPaddingLeft(10f);

        companyTable.addCell(companyCell);

        document.add(companyTable);

        document.add(new Paragraph(" "));

        // ================= CUSTOMER + CHALLAN DETAILS =================

        PdfPTable infoTable = new PdfPTable(2);

        infoTable.setWidthPercentage(100);

        infoTable.setWidths(new float[]{1f, 1f});

        PdfPCell customerCell = new PdfPCell();

        customerCell.addElement(new Paragraph(
                "Delivery Challan For",
                headerFont
        ));

        customerCell.addElement(new Paragraph(
                challan.getCustomerName(),
                headerFont
        ));

        customerCell.addElement(new Paragraph(
                challan.getCustomerAddress(),
                normalFont
        ));

        customerCell.addElement(new Paragraph(
                "GST : " + challan.getCustomerGST(),
                normalFont
        ));

        customerCell.addElement(new Paragraph(
                "Phone : " + challan.getCustomerPhone(),
                normalFont
        ));

        Paragraph emailPara = new Paragraph(
                "Email : " + challan.getCustomerEmail(),
                normalFont
        );

        emailPara.setSpacingAfter(8f);

        customerCell.addElement(emailPara);

        infoTable.addCell(customerCell);

        PdfPCell challanCell = new PdfPCell();

        challanCell.addElement(new Paragraph(
                "Challan Details",
                headerFont
        ));

        challanCell.addElement(new Paragraph(
                "Challan No : " + challan.getChallanNumber(),
                normalFont
        ));

        challanCell.addElement(new Paragraph(
                "Date : " + challan.getChallanDate(),
                normalFont
        ));

        challanCell.addElement(new Paragraph(
                "Vehicle No : " + challan.getVehicleNumber(),
                normalFont
        ));

        challanCell.addElement(new Paragraph(
                "Transport : " + challan.getTransportName(),
                normalFont
        ));

        challanCell.addElement(new Paragraph(
                "Place Of Supply : " + challan.getPlaceOfSupply(),
                normalFont
        ));

        infoTable.addCell(challanCell);

        document.add(infoTable);

        document.add(new Paragraph(" "));

        // ================= ITEM TABLE =================

        // Delivery Challan is a dispatch copy: show only
        // Sr.No, Item Name, HSN and Quantity. Rate / CGST / SGST /
        // IGST / Amount are intentionally hidden on the DC PDF.
        PdfPTable itemTable = new PdfPTable(4);

        itemTable.setWidthPercentage(100);

        itemTable.setWidths(
                new float[]{0.5f, 3f, 1.5f, 1.5f}
        );

        String[] headers = {
        	    "#",
        	    "Item",
        	    "HSN",
        	    "Qty"
        	};

        for (String h : headers) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(h, headerFont)
            );

            cell.setBackgroundColor(Color.LIGHT_GRAY);

            itemTable.addCell(cell);
        }

        int sr = 1;

        BigDecimal totalQty = BigDecimal.ZERO;

        for (DeliveryChallanItem item : challan.getItems()) {

            BigDecimal qty = safe(item.getQuantity());

            itemTable.addCell(String.valueOf(sr++));

            itemTable.addCell(
                    safeStr(item.getDescription())
            );

            itemTable.addCell(
                    safeStr(item.getHsnCode())
            );

            itemTable.addCell(
                    qty.setScale(2, RoundingMode.HALF_UP).toString()
            );

            totalQty = totalQty.add(qty);
        }

        PdfPCell totalCell = new PdfPCell(
                new Phrase("Total Quantity", headerFont)
        );

        totalCell.setColspan(3);

        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        itemTable.addCell(totalCell);

        itemTable.addCell(totalQty.toString());

        document.add(itemTable);

        document.add(new Paragraph(" "));

        // ================= TERMS =================

        PdfPTable termsTable = new PdfPTable(2);

        termsTable.setWidthPercentage(100);

        PdfPCell commentCell = new PdfPCell();

        commentCell.addElement(new Paragraph(
                "Comments",
                headerFont
        ));

        commentCell.addElement(new Paragraph(
                challan.getDescriptions(),
                normalFont
        ));

        termsTable.addCell(commentCell);

        PdfPCell termsCell = new PdfPCell();

        termsCell.addElement(new Paragraph(
                "Terms And Conditions",
                headerFont
        ));

        termsCell.addElement(new Paragraph(
                "Thank you for doing business with us.",
                normalFont
        ));

        Paragraph goodsPara = new Paragraph(
                "Goods once sold will not be taken back.",
                normalFont
        );

      //  goodsPara.setSpacingBefore(6f);

        goodsPara.setSpacingAfter(6f);

        termsCell.addElement(goodsPara);

        termsTable.addCell(termsCell);

        document.add(termsTable);

        document.add(new Paragraph(" "));

        // ================= SIGNATURE TABLE =================

        PdfPTable signTable = new PdfPTable(3);

        signTable.setWidthPercentage(100);

        PdfPCell receivedCell = new PdfPCell();

        receivedCell.setFixedHeight(80);

        receivedCell.addElement(new Paragraph(
                "Received By",
                headerFont
        ));

        receivedCell.addElement(new Paragraph(
                "\nName:",
                normalFont
        ));

        signTable.addCell(receivedCell);

        PdfPCell deliveredCell = new PdfPCell();

        deliveredCell.setFixedHeight(80);

        deliveredCell.addElement(new Paragraph(
                "Delivered By",
                headerFont
        ));

        deliveredCell.addElement(new Paragraph(
                "\nName:",
                normalFont
        ));

        signTable.addCell(deliveredCell);

        PdfPCell authCell = new PdfPCell();

        authCell.setFixedHeight(80);

        authCell.addElement(new Paragraph(
                "For SANDHYA ENGINEERING",
                headerFont
        ));

        authCell.addElement(new Paragraph(
                "\nName:",
                normalFont
        ));

        signTable.addCell(authCell);

        document.add(signTable);

        document.close();

        return out.toByteArray();
    }


    // ================= HELPERS =================

    // Prefer the admin-configured NumberSeries; if it has no row or
    // throws, fall back to the legacy DocumentNumberService so that
    // challan creation never fails because of numbering.
    private String nextChallanNumber() {

        try {

            String number =
                    numberSeriesService.next(DocumentType.DELIVERY_CHALLAN);

            if (number != null && !number.isBlank()) {

                return number;
            }

        } catch (Exception e) {

            log.warn(
                    "NumberSeries lookup failed for DELIVERY_CHALLAN; "
                            + "falling back to legacy numbering",
                    e
            );
        }

        DeliveryChallan last =
                repo.findTopByOrderByIdDesc();

        return documentNumberService.generateNumber(
                "DC",
                last != null ? last.getChallanNumber() : null
        );
    }

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

    PdfPCell cell = new PdfPCell(
            new Phrase(
                    text,
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    )
            )
    );

    cell.setPaddingTop(6f);

    cell.setPaddingBottom(6f);

    cell.setPaddingLeft(5f);

    cell.setPaddingRight(5f);

    cell.setVerticalAlignment(
            Element.ALIGN_MIDDLE
    );

    return cell;
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
    Font headerFont =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    8
            );

    public DeliveryChallan getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Delivery Challan Not Found"
                        )
                );
    }
}