package org.example.islamicf.config;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.User;
import org.example.islamicf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    // You can override these via env or application.properties
    @Value("${app.admin.email:admin1@halal.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin1123}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdmin(UserRepository users) {
        return args -> {
            users.findByEmail(adminEmail).ifPresentOrElse(
                    u -> {
                        /* already exists -> do nothing */ },
                    () -> {
                        User admin = new User();
                        admin.setEmail(adminEmail);
                        admin.setPassword(passwordEncoder.encode(adminPassword)); // BCrypt
                        admin.setRole("ADMIN"); // IMPORTANT: keep "ADMIN" (no "ROLE_" prefix in DB)
                        admin.setFirstName("Admin");
                        admin.setLastName("User");
                        admin.setEnabled(true);
                        users.save(admin);
                        System.out.println("✅ Seeded admin: " + adminEmail);
                    });
        };
    }
}
