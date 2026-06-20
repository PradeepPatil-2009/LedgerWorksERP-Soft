package com.ledger.ledgerworks.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security coverage for the new admin-only configuration modules:
 *   - GET /api/number-series (ADMIN) -> 401 without a token, 200 with an ADMIN token.
 *   - GET /api/company-settings (authenticated) -> 401 without a token.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NewModulesSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void numberSeriesWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/number-series"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void numberSeriesWithAdminTokenIsOk() throws Exception {
        String adminToken = jwtUtil.generateToken("admin", "ADMIN");

        mockMvc.perform(get("/api/number-series")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void companySettingsWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/company-settings"))
                .andExpect(status().isUnauthorized());
    }
}
