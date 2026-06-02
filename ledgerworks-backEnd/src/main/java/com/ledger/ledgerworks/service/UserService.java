package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ FIXED LOGIN
    public User login(String username, String password) {

        User user = repository.findByUsername(username);
        if(user==null) {
                throw new RuntimeException("User not found");
        }

        // ✅ IMPORTANT CHANGE
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    // ✅ CREATE USER (ENCODE PASSWORD)
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }
}