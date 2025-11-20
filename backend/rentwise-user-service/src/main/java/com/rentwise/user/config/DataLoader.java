package com.rentwise.user.config;

import com.rentwise.user.model.User;
import com.rentwise.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader - This is just for testing purpose
 * Creates a default admin user on application startup if it doesn't exist
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            logger.info("Creating default admin user...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // This is just for testing purpose
            admin.setEmail("admin@rentwise.com");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            logger.info("Default admin user created: username=admin");
        } else {
            logger.info("Admin user already exists, skipping creation");
        }
    }
}

