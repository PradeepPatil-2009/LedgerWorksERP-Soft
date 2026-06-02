/*package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.repository.UserRepository;
import com.ledger.ledgerworks.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(username, user.getRole().name());
    }
}

*/

package com.ledger.ledgerworks.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String login(String username, String password) {

        // Dummy login logic
        if ("admin".equals(username) && "123".equals(password)) {
            return "dummy-jwt-token";
        }

        throw new RuntimeException("Invalid credentials");
    }
}

