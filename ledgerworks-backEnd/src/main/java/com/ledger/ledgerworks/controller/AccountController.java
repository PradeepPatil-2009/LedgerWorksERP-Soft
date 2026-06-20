package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import com.ledger.ledgerworks.util.PasswordPolicy;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Self-service account actions for the currently authenticated user. The acting
 * username is always taken from the security context (the JWT), never from the
 * request body, so any signed-in user may change only their own password.
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= CHANGE PASSWORD =================

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {

        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username);

        if (user == null
                || request.getOldPassword() == null
                || !passwordEncoder.matches(
                        request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Current password is incorrect"));
        }

        // Current password verified — now enforce the policy on the new password.
        // (400 with the policy message, via BusinessExceptionHandler.)
        PasswordPolicy.validate(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }

    // Request payload for the change-password endpoint.
    public static class ChangePasswordRequest {

        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
