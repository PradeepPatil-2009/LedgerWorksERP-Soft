package com.ledger.ledgerworks.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("No JWT_SECRET was set; using an ephemeral random key. "
                    + "Tokens will not survive an application restart. "
                    + "Set JWT_SECRET for production.");
        } else {
            key = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }

    public String generateToken(String username, String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /** @return access-token lifetime in milliseconds (used for the access_token cookie Max-Age). */
    public long getExpiration() {
        return expiration;
    }

    /** @return refresh-token lifetime in milliseconds (used for the refresh_token cookie Max-Age). */
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}