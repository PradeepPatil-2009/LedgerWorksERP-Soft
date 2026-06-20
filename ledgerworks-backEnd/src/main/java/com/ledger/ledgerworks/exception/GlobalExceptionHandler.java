package com.ledger.ledgerworks.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.ledger.ledgerworks.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Authorization denials — including method-security ({@code @PreAuthorize})
     * which throws {@code AuthorizationDeniedException} (a RuntimeException) — must
     * surface as 403, not be swallowed by the generic handler below as a 400.
     * A generic message is returned so the underlying reason is not leaked.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>("ERROR", "Access denied", null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex) {

        // Log the full detail server-side; return the message to the client as
        // before (many business flows rely on the message text).
        log.warn("Unhandled runtime exception", ex);

        ApiResponse<Object> response = new ApiResponse<>(
                "ERROR",
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
