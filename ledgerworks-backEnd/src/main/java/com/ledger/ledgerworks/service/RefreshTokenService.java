package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.RefreshToken;
import com.ledger.ledgerworks.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages opaque server-side refresh tokens. A refresh token is a random UUID
 * persisted with an expiry and a revoked flag; it is exchanged for a fresh access
 * JWT on POST /api/auth/refresh and revoked on logout.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /** Refresh token lifetime in milliseconds (default 7 days). */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Creates and persists a new refresh token for the given user, expiring
     * {@code refreshExpirationMs} from now.
     */
    public RefreshToken create(String username) {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID().toString(),
                username,
                Instant.now().plusMillis(refreshExpirationMs));
        return refreshTokenRepository.save(token);
    }

    /**
     * Returns the active (existing, non-revoked, non-expired) token matching the
     * given UUID, or {@link Optional#empty()} otherwise.
     */
    public Optional<RefreshToken> validate(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByTokenValue(tokenValue)
                .filter(t -> !t.isRevoked() && !t.isExpired());
    }

    /** Marks the matching token as revoked, if present. */
    public void revoke(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return;
        }
        refreshTokenRepository.findByTokenValue(tokenValue).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    /**
     * Rotates a refresh token: if {@code oldTokenValue} is currently valid
     * (existing, non-revoked, non-expired), revokes it and mints a brand-new
     * token (new opaque UUID) for the same user, returning the new token.
     * If the presented token is invalid/revoked/expired, returns
     * {@link Optional#empty()} and nothing is changed.
     */
    public Optional<RefreshToken> rotate(String oldTokenValue) {
        return validate(oldTokenValue).map(existing -> {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
            return create(existing.getUsername());
        });
    }
}
