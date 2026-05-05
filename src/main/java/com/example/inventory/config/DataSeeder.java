package com.example.inventory.config;

import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("No users found. Seeding initial ADMIN user...");
            User admin = User.builder()
                    .name("Super Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .mobile("1234567890")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("ADMIN user seeded: email: admin@example.com, password: admin123");
        }
    }
}
