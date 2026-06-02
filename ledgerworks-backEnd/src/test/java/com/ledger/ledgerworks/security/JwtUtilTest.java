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

        // Flip the final character of the signature to invalidate it.
        char last = token.charAt(token.length() - 1);
        char swapped = last == 'A' ? 'B' : 'A';
        String tampered = token.substring(0, token.length() - 1) + swapped;

        assertFalse(jwtUtil.validateToken(tampered));
    }
}
