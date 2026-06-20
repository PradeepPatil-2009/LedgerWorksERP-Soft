package com.ledger.ledgerworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.ledgerworks.entity.CompanySettings;
import com.ledger.ledgerworks.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Saves company settings (PUT, ADMIN only) then reads them back (GET) and
 * verifies the persisted company name survives the round trip.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanySettingsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void savesThenReadsBackCompanyName() throws Exception {
        String adminToken = jwtUtil.generateToken("admin", "ADMIN");

        CompanySettings settings = new CompanySettings();
        settings.setCompanyName("LedgerWorks Test Pvt Ltd");
        settings.setGstNumber("27ABCDE1234F1Z5");
        settings.setPanNumber("ABCDE1234F");

        // SAVE (PUT requires ADMIN)
        mockMvc.perform(put("/api/company-settings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("LedgerWorks Test Pvt Ltd"));

        // READ BACK (GET requires an authenticated user)
        mockMvc.perform(get("/api/company-settings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("LedgerWorks Test Pvt Ltd"))
                .andExpect(jsonPath("$.gstNumber").value("27ABCDE1234F1Z5"));
    }
}
