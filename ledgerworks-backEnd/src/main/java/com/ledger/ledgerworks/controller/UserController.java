package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.enums.Role;
import com.ledger.ledgerworks.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ 1. GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ 2. CREATE USER (PASSWORD ENCODED)
    @PostMapping
    public User createUser(@RequestBody User user) {

        // 🔐 Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // ✅ 3. DELETE USER
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return "User not found ❌";
        }

        userRepository.deleteById(id);
        return "User deleted successfully ✅";
    }
    //4 update user
 /*   @PutMapping("/{id}/role")
    public User updateRole(@PathVariable Long id, @RequestParam String role) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(Role.valueOf(role.toUpperCase()));
        return userRepository.save(user);
    }*/
    @PutMapping("/{id}/role")
    public User updateRole(@PathVariable Long id,
                           @RequestParam String role) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 Prevent admin role change
        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new RuntimeException("Admin role cannot be changed");
        }

        user.setRole(Role.valueOf(role.toUpperCase()));

        return userRepository.save(user);
    }
}