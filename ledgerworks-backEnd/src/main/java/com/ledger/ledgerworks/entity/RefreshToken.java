package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Opaque server-side refresh token. The {@code tokenValue} is a random UUID handed
 * to the browser in the {@code refresh_token} cookie; it is NOT a JWT. The row is
 * looked up on POST /api/auth/refresh and revoked on logout.
 *
 * <p>The column is named {@code token_value} to avoid the reserved word {@code token}
 * on some databases.</p>
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_value", unique = true, nullable = false)
    private String tokenValue;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    public RefreshToken() {
    }

    public RefreshToken(String tokenValue, String username, Instant expiryDate) {
        this.tokenValue = tokenValue;
        this.username = username;
        this.expiryDate = expiryDate;
        this.revoked = false;
    }

    public Long getId() {
        return id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    /** @return {@code true} when this token is past its expiry instant. */
    public boolean isExpired() {
        return expiryDate == null || Instant.now().isAfter(expiryDate);
    }
}
