package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.LoginRequest;
import com.ledger.ledgerworks.dto.LoginResponse;
import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import com.ledger.ledgerworks.security.JwtUtil;
import com.ledger.ledgerworks.service.LoginAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoint. Verifies the user against the database (BCrypt) and,
 * on success, issues a real signed JWT carrying the user's role.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

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

        return ResponseEntity.ok(
                new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token));
    }
}
