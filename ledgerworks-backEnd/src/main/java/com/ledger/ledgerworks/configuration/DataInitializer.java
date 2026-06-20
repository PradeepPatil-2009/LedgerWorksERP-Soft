package com.ledger.ledgerworks.configuration;

import com.ledger.ledgerworks.entity.User;
import com.ledger.ledgerworks.enums.Role;
import com.ledger.ledgerworks.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds a default ADMIN account on first run. Idempotent: it never deletes
 * existing users, so accounts created via User Management survive restarts.
 */
@Configuration
public class DataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(DataInitializer.class);

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                repo.save(admin);
                log.info("Seeded default admin user 'admin' "
                        + "(change the default password for production)");
            }
        };
    }
}
