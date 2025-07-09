package com.example.jwtuserservice.config;

import com.example.jwtuserservice.entity.Login;
import com.example.jwtuserservice.entity.User;
import com.example.jwtuserservice.repository.LoginRepository;
import com.example.jwtuserservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoginRepository loginRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing database...");
        
        try {
            // Check if users already exist
            long userCount = loginRepository.count();
            logger.info("Current user count in database: {}", userCount);
            
            if (userCount == 0) {
                logger.info("No users found, creating sample users...");
                
                // Create users
                User user1 = new User();
                user1.setFirstName("John");
                user1.setLastName("Doe");
                user1.setAge(30);
                user1.setBirthday(new Date());
                user1 = userRepository.save(user1);
                logger.info("Created user: {}", user1.getId());
                
                User user2 = new User();
                user2.setFirstName("Jane");
                user2.setLastName("Smith");
                user2.setAge(25);
                user2.setBirthday(new Date());
                user2 = userRepository.save(user2);
                logger.info("Created user: {}", user2.getId());
                
                User user3 = new User();
                user3.setFirstName("Mike");
                user3.setLastName("Johnson");
                user3.setAge(35);
                user3.setBirthday(new Date());
                user3 = userRepository.save(user3);
                logger.info("Created user: {}", user3.getId());
                
                // Create login credentials
                String encodedPassword = passwordEncoder.encode("password123");
                logger.info("Encoded password for all users: {}", encodedPassword);
                
                Login login1 = new Login();
                login1.setUsername("john.doe");
                login1.setPassword(encodedPassword);
                login1.setUser(user1);
                loginRepository.save(login1);
                logger.info("Created login for john.doe");
                
                Login login2 = new Login();
                login2.setUsername("jane.smith");
                login2.setPassword(encodedPassword);
                login2.setUser(user2);
                loginRepository.save(login2);
                logger.info("Created login for jane.smith");
                
                Login login3 = new Login();
                login3.setUsername("mike.johnson");
                login3.setPassword(encodedPassword);
                login3.setUser(user3);
                loginRepository.save(login3);
                logger.info("Created login for mike.johnson");
                
                logger.info("Sample users created successfully");
            } else {
                logger.info("Users already exist in database");
                
                // Verify john.doe exists
                boolean johnExists = loginRepository.existsByUsername("john.doe");
                logger.info("User 'john.doe' exists: {}", johnExists);
                
                if (johnExists) {
                    Login johnLogin = loginRepository.findByUsername("john.doe").orElse(null);
                    if (johnLogin != null) {
                        logger.info("John's password hash: {}", johnLogin.getPassword());
                        boolean passwordMatches = passwordEncoder.matches("password123", johnLogin.getPassword());
                        logger.info("Password 'password123' matches: {}", passwordMatches);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error during database initialization", e);
            throw e;
        }
    }
} 