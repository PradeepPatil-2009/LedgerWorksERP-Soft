package com.ledger.ledgerworks.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory brute-force protection. Tracks consecutive failed login attempts
 * per username (lowercased) and blocks further attempts once the configured
 * threshold is reached inside a rolling time window. The counter auto-resets
 * once the window elapses since the first recorded failure.
 *
 * <p>No external dependencies are used; state lives in a {@link ConcurrentHashMap}
 * and resets on application restart.</p>
 */
@Service
public class LoginAttemptService {

    /** Maximum failed attempts allowed within the window before blocking. */
    private static final int MAX_ATTEMPTS = 5;

    /** Rolling window length in milliseconds (15 minutes). */
    private static final long WINDOW_MS = 15L * 60L * 1000L;

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    /** Mutable per-user counter holding the failure count and the first-failure timestamp. */
    private static final class Attempt {
        int count;
        long firstFailAt;

        Attempt(long firstFailAt) {
            this.count = 1;
            this.firstFailAt = firstFailAt;
        }
    }

    private static String normalize(String key) {
        return key == null ? "" : key.trim().toLowerCase();
    }

    /**
     * @return {@code true} when the user has reached the failure threshold within
     *         the active window. Returns {@code false} once the window has elapsed.
     */
    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(normalize(key));
        if (attempt == null) {
            return false;
        }
        if (System.currentTimeMillis() - attempt.firstFailAt >= WINDOW_MS) {
            // Window elapsed: stale entry no longer blocks.
            return false;
        }
        return attempt.count >= MAX_ATTEMPTS;
    }

    /** Records a failed login attempt, auto-resetting the counter when the window has elapsed. */
    public void recordFailure(String key) {
        String normalized = normalize(key);
        long now = System.currentTimeMillis();
        attempts.compute(normalized, (k, existing) -> {
            if (existing == null || now - existing.firstFailAt >= WINDOW_MS) {
                return new Attempt(now);
            }
            existing.count++;
            return existing;
        });
    }

    /** Clears the failure counter for the given user after a successful login. */
    public void recordSuccess(String key) {
        attempts.remove(normalize(key));
    }
}
