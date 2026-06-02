package com.ledger.ledgerworks.configuration;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.enums.Role;
import com.ledger.ledgerworks.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            // 🔥 Always recreate admin (safe for now)
            repo.deleteAll();

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123")); // ✅ encoded
            admin.setRole(Role.ADMIN);

            repo.save(admin);

            System.out.println("✅ Admin created: admin / admin123");
        };
    }
}