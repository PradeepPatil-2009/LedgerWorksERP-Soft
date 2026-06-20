package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.dto.ImportResult;
import com.ledger.ledgerworks.entity.Customer;
import com.ledger.ledgerworks.entity.ItemMaster;
import com.ledger.ledgerworks.entity.Vendor;
import com.ledger.ledgerworks.repository.CustomerRepository;
import com.ledger.ledgerworks.repository.ItemMasterRepository;
import com.ledger.ledgerworks.repository.VendorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Bulk data import for Customers, Vendors and Items.
 *
 * Accepts .xlsx (POI) and .csv uploads. Each row is mapped defensively:
 * blank/duplicate/invalid rows are skipped and recorded in the result,
 * the import never aborts the whole file for one bad row.
 */
@Service
public class ImportService {

    @Autowired
    private ExcelParserService excelParserService;

    @Autowired
    private CsvParserService csvParserService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    // =====================================================
    // PUBLIC ENTRY POINTS
    // =====================================================

    public ImportResult importCustomers(MultipartFile file) {

        ImportResult result = new ImportResult();

        List<Map<String, String>> rows = readRows(file, result);

        int line = 1;

        for (Map<String, String> row : rows) {

            line++;

            try {

                String name = get(row, "name", "customername", "customer");

                if (isBlank(name)) {
                    result.addSkipped("Row " + line + ": missing name");
                    continue;
                }

                if (customerRepository.findByName(name).isPresent()) {
                    result.addSkipped(
                            "Row " + line + ": customer '" + name
                                    + "' already exists");
                    continue;
                }

                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(get(row, "email", "emailid"));
                customer.setPhone(get(row, "phone", "mobile", "contact"));
                customer.setAddress(get(row, "address"));
                customer.setState(get(row, "state"));
                customer.setStateCode(get(row, "statecode", "state code"));
                customer.setGstType(get(row, "gsttype", "gst type"));

                String gst = get(row, "gstnumber", "gst", "gstno", "gst number");

                if (!isBlank(gst)) {

                    if (customerRepository.findByGstNumber(gst).isPresent()) {
                        result.addSkipped(
                                "Row " + line + ": GST '" + gst
                                        + "' already exists");
                        continue;
                    }

                    customer.setGstNumber(gst);
                }

                customerRepository.save(customer);
                result.addImported();

            } catch (Exception ex) {
                result.addSkipped("Row " + line + ": " + ex.getMessage());
            }
        }

        return result;
    }

    public ImportResult importVendors(MultipartFile file) {

        ImportResult result = new ImportResult();

        List<Map<String, String>> rows = readRows(file, result);

        int line = 1;

        for (Map<String, String> row : rows) {

            line++;

            try {

                String name = get(row, "name", "vendorname", "vendor");

                if (isBlank(name)) {
                    result.addSkipped("Row " + line + ": missing name");
                    continue;
                }

                String email = get(row, "email", "emailid");

                if (!isBlank(email)
                        && vendorRepository.findByEmail(email).isPresent()) {
                    result.addSkipped(
                            "Row " + line + ": vendor email '" + email
                                    + "' already exists");
                    continue;
                }

                Vendor vendor = new Vendor();
                vendor.setName(name);
                vendor.setEmail(email);
                vendor.setPhone(get(row, "phone", "mobile", "contact"));
                vendor.setAddress(get(row, "address"));
                vendor.setGstNumber(
                        get(row, "gstnumber", "gst", "gstno", "gst number"));
                vendor.setState(get(row, "state"));
                vendor.setStateCode(get(row, "statecode", "state code"));
                vendor.setGstType(get(row, "gsttype", "gst type"));

                vendorRepository.save(vendor);
                result.addImported();

            } catch (Exception ex) {
                result.addSkipped("Row " + line + ": " + ex.getMessage());
            }
        }

        return result;
    }

    public ImportResult importItems(MultipartFile file) {

        ImportResult result = new ImportResult();

        List<Map<String, String>> rows = readRows(file, result);

        int line = 1;

        for (Map<String, String> row : rows) {

            line++;

            try {

                String itemName = get(row, "itemname", "name", "item name", "item");

                if (isBlank(itemName)) {
                    result.addSkipped("Row " + line + ": missing item name");
                    continue;
                }

                if (itemMasterRepository.existsByItemName(itemName)) {
                    result.addSkipped(
                            "Row " + line + ": item '" + itemName
                                    + "' already exists");
                    continue;
                }

                ItemMaster item = new ItemMaster();
                item.setItemName(itemName);
                item.setItemCode(get(row, "itemcode", "code", "item code"));
                item.setHsnCode(get(row, "hsncode", "hsn", "hsn code"));
                item.setUnit(get(row, "unit", "uom"));
                item.setCategory(get(row, "category"));

                String status = get(row, "status");
                item.setStatus(isBlank(status) ? "ACTIVE" : status);

                item.setPurchaseRate(
                        parseDecimal(get(row, "purchaserate", "purchase rate",
                                "purchaseprice")));
                item.setSaleRate(
                        parseDecimal(get(row, "salerate", "sale rate",
                                "saleprice", "salesrate")));

                BigDecimal stock = parseDecimal(
                        get(row, "currentstock", "stock", "openingstock",
                                "quantity", "qty"));
                item.setCurrentStock(stock == null ? BigDecimal.ZERO : stock);

                if (isBlank(item.getItemCode())) {
                    item.setItemCode("ITM-" + System.currentTimeMillis());
                }

                itemMasterRepository.save(item);
                result.addImported();

            } catch (Exception ex) {
                result.addSkipped("Row " + line + ": " + ex.getMessage());
            }
        }

        return result;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private List<Map<String, String>> readRows(
            MultipartFile file,
            ImportResult result) {

        if (file == null || file.isEmpty()) {
            result.addSkipped("Uploaded file is empty");
            return List.of();
        }

        String fileName = file.getOriginalFilename();
        String lower = fileName == null ? "" : fileName.toLowerCase();

        try {

            if (lower.endsWith(".csv")) {
                return csvParserService.parse(file);
            }

            if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
                return excelParserService.parse(file);
            }

            // Unknown / missing extension: only fall back to the Excel parser
            // when the file actually looks like a ZIP-based .xlsx (magic bytes
            // 50 4B 03 04). Otherwise reject rather than blindly trying both
            // parsers on arbitrary content.
            if (looksLikeXlsx(file)) {
                return excelParserService.parse(file);
            }

            result.addSkipped(
                    "Unsupported file type: please upload a "
                            + ".xlsx, .xls or .csv file");
            return List.of();

        } catch (Exception ex) {
            result.addSkipped(
                    "Unable to read file: " + ex.getMessage());
            return List.of();
        }
    }

    /**
     * Returns true when the upload starts with the ZIP local-file-header magic
     * bytes (50 4B 03 04) used by the .xlsx (OOXML) container format.
     */
    private boolean looksLikeXlsx(MultipartFile file) {

        try (java.io.InputStream in = file.getInputStream()) {

            byte[] magic = new byte[4];
            int read = in.read(magic);

            return read == 4
                    && magic[0] == (byte) 0x50
                    && magic[1] == (byte) 0x4B
                    && magic[2] == (byte) 0x03
                    && magic[3] == (byte) 0x04;

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns the first non-blank value for any of the candidate header keys.
     */
    private String get(Map<String, String> row, String... keys) {

        if (row == null) {
            return null;
        }

        for (String key : keys) {
            String value = row.get(key);
            if (!isBlank(value)) {
                return value.trim();
            }
        }

        return null;
    }

    private BigDecimal parseDecimal(String value) {

        if (isBlank(value)) {
            return null;
        }

        try {
            // Strip currency symbols, commas and spaces.
            String cleaned = value.replaceAll("[^0-9.\\-]", "");
            if (cleaned.isEmpty() || cleaned.equals("-")) {
                return null;
            }
            return new BigDecimal(cleaned);
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
