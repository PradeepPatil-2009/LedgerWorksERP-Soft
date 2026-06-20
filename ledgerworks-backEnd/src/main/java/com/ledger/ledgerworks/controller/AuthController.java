package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.LoginRequest;
import com.ledger.ledgerworks.dto.LoginResponse;
import com.ledger.ledgerworks.entity.RefreshToken;
import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import com.ledger.ledgerworks.security.JwtUtil;
import com.ledger.ledgerworks.service.LoginAttemptService;
import com.ledger.ledgerworks.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication endpoint. Verifies the user against the database (BCrypt) and,
 * on success, issues a real signed JWT carrying the user's role.
 *
 * <p>In addition to the JSON body (kept for backward compatibility), login sets
 * two HttpOnly cookies: {@code access_token} (the access JWT) and
 * {@code refresh_token} (an opaque server-side UUID). The refresh endpoint mints a
 * fresh access cookie from a valid refresh cookie; logout revokes and clears both.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ACCESS_COOKIE = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {

        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String username = request.getUsername().trim();

        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many failed attempts. Try again later.");
        }

        User user = userRepository.findByUsername(username);

        boolean valid = user != null
                && passwordEncoder.matches(request.getPassword().trim(), user.getPassword());

        if (!valid) {
            loginAttemptService.recordFailure(username);
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        loginAttemptService.recordSuccess(username);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        // Issue cookies: access JWT + opaque refresh UUID.
        RefreshToken refreshToken = refreshTokenService.create(user.getUsername());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_COOKIE, token, jwtUtil.getExpiration() / 1000).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(REFRESH_COOKIE, refreshToken.getTokenValue(),
                        jwtUtil.getRefreshExpiration() / 1000).toString());

        return ResponseEntity.ok(
                new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request,
                                     HttpServletResponse response) {

        String refreshValue = readCookie(request, REFRESH_COOKIE);

        Optional<RefreshToken> valid = refreshTokenService.validate(refreshValue);
        if (valid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUsername(valid.get().getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_COOKIE, token, jwtUtil.getExpiration() / 1000).toString());

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {

        String refreshValue = readCookie(request, REFRESH_COOKIE);
        refreshTokenService.revoke(refreshValue);

        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(ACCESS_COOKIE).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(REFRESH_COOKIE).toString());

        return ResponseEntity.ok().build();
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private ResponseCookie expireCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
