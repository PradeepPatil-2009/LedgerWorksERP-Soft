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
 * Verifies the stateless JWT chain end to end against a real protected endpoint
 * (GET /api/customers): no token -> 401, valid ADMIN bearer token -> 200.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithValidAdminTokenIsOk() throws Exception {
        String token = jwtUtil.generateToken("admin", "ADMIN");

        mockMvc.perform(get("/api/customers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
