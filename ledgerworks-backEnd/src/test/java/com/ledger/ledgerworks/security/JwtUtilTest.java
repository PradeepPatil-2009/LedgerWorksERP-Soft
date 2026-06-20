package com.ledger.ledgerworks.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies token generation, claim extraction and validation on the
 * Spring-wired {@link JwtUtil} bean (so the configured secret/expiration apply).
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateThenValidateTokenIsTrue() {
        String token = jwtUtil.generateToken("admin", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtUtil.validateToken(token));

        assertEquals("admin", jwtUtil.extractAllClaims(token).getSubject());
        assertEquals("ADMIN", jwtUtil.extractAllClaims(token).get("role", String.class));
    }

    @Test
    void garbageTokenValidatesFalse() {
        assertFalse(jwtUtil.validateToken("this.is.not.a.jwt"));
    }

    @Test
    void tamperedTokenValidatesFalse() {
        String token = jwtUtil.generateToken("admin", "ADMIN");

        // Tamper the payload segment: altering the signed content deterministically
        // breaks the signature (flipping the signature's trailing base64 char is
        // unreliable because its low bits are padding and may decode unchanged).
        String[] parts = token.split("\\.");
        char first = parts[1].charAt(0);
        char swapped = first == 'A' ? 'B' : 'A';
        String tamperedPayload = swapped + parts[1].substring(1);
        String tampered = parts[0] + "." + tamperedPayload + "." + parts[2];

        assertFalse(jwtUtil.validateToken(tampered));
    }
}
