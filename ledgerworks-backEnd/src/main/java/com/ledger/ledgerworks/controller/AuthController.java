package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.LoginResponse;
import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {

        User user = userRepository.findByUsername(loginUser.getUsername());

        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid Credentials");
        }

        boolean match = passwordEncoder.matches(
                loginUser.getPassword().trim(),
                user.getPassword()
        );

        if (!match) {
            return ResponseEntity.badRequest().body("Invalid Credentials");
        }

        // ✅ Generate dummy token (IMPORTANT FIX)
        String token = "dummy-token-" + user.getId();

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        token
                )
        );
    }
}