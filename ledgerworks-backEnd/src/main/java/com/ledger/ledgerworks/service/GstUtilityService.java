package com.ledger.ledgerworks.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Self-contained utility that maps the first two digits of a GSTIN to the
 * corresponding Indian state / union-territory name. No database required.
 */
@Service
public class GstUtilityService {

    private static final Map<String, String> STATE_CODES = new HashMap<>();

    static {
        STATE_CODES.put("01", "Jammu and Kashmir");
        STATE_CODES.put("02", "Himachal Pradesh");
        STATE_CODES.put("03", "Punjab");
        STATE_CODES.put("04", "Chandigarh");
        STATE_CODES.put("05", "Uttarakhand");
        STATE_CODES.put("06", "Haryana");
        STATE_CODES.put("07", "Delhi");
        STATE_CODES.put("08", "Rajasthan");
        STATE_CODES.put("09", "Uttar Pradesh");
        STATE_CODES.put("10", "Bihar");
        STATE_CODES.put("11", "Sikkim");
        STATE_CODES.put("12", "Arunachal Pradesh");
        STATE_CODES.put("13", "Nagaland");
        STATE_CODES.put("14", "Manipur");
        STATE_CODES.put("15", "Mizoram");
        STATE_CODES.put("16", "Tripura");
        STATE_CODES.put("17", "Meghalaya");
        STATE_CODES.put("18", "Assam");
        STATE_CODES.put("19", "West Bengal");
        STATE_CODES.put("20", "Jharkhand");
        STATE_CODES.put("21", "Odisha");
        STATE_CODES.put("22", "Chhattisgarh");
        STATE_CODES.put("23", "Madhya Pradesh");
        STATE_CODES.put("24", "Gujarat");
        STATE_CODES.put("25", "Daman and Diu");
        STATE_CODES.put("26", "Dadra and Nagar Haveli");
        STATE_CODES.put("27", "Maharashtra");
        STATE_CODES.put("28", "Andhra Pradesh (Old)");
        STATE_CODES.put("29", "Karnataka");
        STATE_CODES.put("30", "Goa");
        STATE_CODES.put("31", "Lakshadweep");
        STATE_CODES.put("32", "Kerala");
        STATE_CODES.put("33", "Tamil Nadu");
        STATE_CODES.put("34", "Puducherry");
        STATE_CODES.put("35", "Andaman and Nicobar Islands");
        STATE_CODES.put("36", "Telangana");
        STATE_CODES.put("37", "Andhra Pradesh");
        STATE_CODES.put("38", "Ladakh");
        STATE_CODES.put("97", "Other Territory");
        STATE_CODES.put("99", "Centre Jurisdiction");
    }

    // Extract the 2-digit state code from a GSTIN.
    public String codeFromGst(String gst) {

        if (gst == null) {
            return null;
        }

        String trimmed = gst.trim();

        if (trimmed.length() < 2) {
            return null;
        }

        return trimmed.substring(0, 2);
    }

    // Resolve a state name from a full GSTIN.
    public String stateFromGst(String gst) {

        String code = codeFromGst(gst);

        if (code == null) {
            return null;
        }

        return STATE_CODES.get(code);
    }
}
