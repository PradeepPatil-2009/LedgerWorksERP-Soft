package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.LoginRequest;
import com.ledger.ledgerworks.dto.LoginResponse;
import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import com.ledger.ledgerworks.security.JwtUtil;
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

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = userRepository.findByUsername(request.getUsername().trim());

        boolean valid = user != null
                && passwordEncoder.matches(request.getPassword().trim(), user.getPassword());

        if (!valid) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(
                new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token));
    }
}
