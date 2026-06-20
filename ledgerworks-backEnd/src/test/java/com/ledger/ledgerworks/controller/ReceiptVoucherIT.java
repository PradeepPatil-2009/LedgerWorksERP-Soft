package com.ledger.ledgerworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.ledgerworks.entity.ReceiptVoucher;
import com.ledger.ledgerworks.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Posts a Receipt Voucher and confirms it is persisted (auto voucher number
 * assigned) and subsequently returned by GET /api/receipt-vouchers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReceiptVoucherIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postPersistsAndIsReturnedByGet() throws Exception {
        String token = jwtUtil.generateToken("admin", "ADMIN");

        ReceiptVoucher voucher = new ReceiptVoucher();
        voucher.setPartyName("Beta Customer");
        voucher.setAmount(new BigDecimal("1500.00"));
        voucher.setNarration("Receipt voucher IT");

        // CREATE
        mockMvc.perform(post("/api/receipt-vouchers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voucher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.partyName").value("Beta Customer"))
                .andExpect(jsonPath("$.voucherNumber").isNotEmpty());

        // LIST should now contain the persisted voucher
        mockMvc.perform(get("/api/receipt-vouchers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.partyName == 'Beta Customer')]").exists());
    }
}
