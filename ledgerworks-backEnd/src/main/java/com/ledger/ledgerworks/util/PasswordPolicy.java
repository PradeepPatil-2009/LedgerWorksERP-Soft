package com.ledger.ledgerworks.util;

/**
 * Centralised password-strength policy.
 *
 * <p>A valid password is at least 8 characters long and contains at least one
 * letter and at least one digit. Validation failures throw an
 * {@link IllegalArgumentException}; the global {@code BusinessExceptionHandler}
 * turns that into an HTTP 400 carrying the message below.</p>
 */
public final class PasswordPolicy {

    /** Message returned to the caller when a password fails the policy. */
    public static final String MESSAGE =
            "Password must be at least 8 characters and include a letter and a number.";

    private PasswordPolicy() {
    }

    /**
     * Validates the supplied password against the policy.
     *
     * @param pw the raw (un-encoded) password to check
     * @throws IllegalArgumentException if {@code pw} is null/blank, shorter than
     *         8 characters, or missing a letter or a digit
     */
    public static void validate(String pw) {
        if (pw == null || pw.isBlank() || pw.length() < 8) {
            throw new IllegalArgumentException(MESSAGE);
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < pw.length(); i++) {
            char c = pw.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException(MESSAGE);
        }
    }
}
