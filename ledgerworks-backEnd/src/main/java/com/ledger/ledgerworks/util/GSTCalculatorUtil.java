package com.ledger.ledgerworks.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class GSTCalculatorUtil {

    public static Map<String, BigDecimal> calculateGST(
            BigDecimal taxableAmount,
            BigDecimal gstPercent,
            String sellerStateCode,
            String buyerStateCode
    ) {

        Map<String, BigDecimal> result = new HashMap<>();

        BigDecimal gstAmount = taxableAmount
                .multiply(gstPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;

        // SAME STATE → CGST + SGST
        if (sellerStateCode != null &&
                sellerStateCode.equalsIgnoreCase(buyerStateCode)) {

            cgst = gstAmount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            sgst = gstAmount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        } else {

            // DIFFERENT STATE → IGST
            igst = gstAmount;
        }

        BigDecimal grandTotal = taxableAmount.add(gstAmount);

        result.put("taxableAmount", taxableAmount);
        result.put("cgst", cgst);
        result.put("sgst", sgst);
        result.put("igst", igst);
        result.put("grandTotal", grandTotal);

        return result;
    }
}