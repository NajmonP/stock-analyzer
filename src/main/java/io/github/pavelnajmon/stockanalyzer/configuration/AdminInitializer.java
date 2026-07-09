package io.github.pavelnajmon.stockanalyzer.configuration;

import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import io.github.pavelnajmon.stockanalyzer.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
public class AdminInitializer {

    @Bean
    ApplicationRunner createAdminUser(AdminProperties adminProperties, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (isBlank(adminProperties.getUsername())
                    || isBlank(adminProperties.getEmail())
                    || isBlank(adminProperties.getPassword())) {
                return;
            }

            boolean adminExists = userRepository.existsByUserRole(UserRole.ADMIN);

            if (adminExists) {
                return;
            }

            User admin = new User();
            admin.setUsername(adminProperties.getUsername());
            admin.setEmail(adminProperties.getEmail());
            admin.setPasswordHash(passwordEncoder.encode(adminProperties.getPassword()));
            admin.setUserRole(UserRole.ADMIN);
            admin.setCreatedAt(Instant.now());

            userRepository.save(admin);
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}