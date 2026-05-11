package com.example.inventory.config;

import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataSeeder is responsible for initializing the database with default data.
 * It implements CommandLineRunner, meaning its run() method will execute automatically
 * on application startup, right after the Spring context is loaded.
 */
@Component // Marks this class as a Spring-managed component so it's picked up during classpath scanning
@RequiredArgsConstructor // Lombok annotation to automatically generate a constructor injecting final fields
@Slf4j // Lombok annotation that provides a 'log' variable for logging messages
public class DataSeeder implements CommandLineRunner {

    // Repository to interact with the users table
    private final UserRepository userRepository;
    
    // Encoder to securely hash passwords before saving them
    private final PasswordEncoder passwordEncoder;

    /**
     * This method runs automatically when the Spring Boot application starts.
     * @param args Command line arguments passed to the application
     */
    @Override
    public void run(String... args) throws Exception {
        // Check if there are any users in the database
        if (userRepository.count() == 0) {
            log.info("No users found. Seeding initial ADMIN user...");
            
            // Build the default admin user using the Builder pattern provided by Lombok @Builder
            User admin = User.builder()
                    .name("Super Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123")) // Hash the password!
                    .mobile("1234567890")
                    .role(Role.ADMIN)
                    .build();
                    
            // Save the newly created admin user to the database
            userRepository.save(admin);
            log.info("ADMIN user seeded: email: admin@example.com, password: admin123");
        }
    }
}
