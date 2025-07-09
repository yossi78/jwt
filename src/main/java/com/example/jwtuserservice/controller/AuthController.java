package com.example.jwtuserservice.controller;

import com.example.jwtuserservice.dto.AuthRequest;
import com.example.jwtuserservice.dto.AuthResponse;
import com.example.jwtuserservice.service.AuthService;
import com.example.jwtuserservice.repository.LoginRepository;
import com.example.jwtuserservice.entity.Login;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private LoginRepository loginRepository;
    
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("POST /api/auth/signin - User attempting to sign in: {}", authRequest.getUsername());
        try {
            AuthResponse response = authService.authenticate(authRequest);
            logger.info("POST /api/auth/signin - User signed in successfully: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("POST /api/auth/signin - Sign in failed for user: {}", authRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("POST /api/auth/signup - User attempting to sign up: {}", authRequest.getUsername());
        try {
            AuthResponse response = authService.signUp(authRequest);
            logger.info("POST /api/auth/signup - User signed up successfully: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("POST /api/auth/signup - Sign up failed for user: {}", authRequest.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("POST /api/auth/refresh - Refreshing token");
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.warn("POST /api/auth/refresh - Invalid authorization header");
                return ResponseEntity.badRequest().build();
            }
            
            String refreshToken = authorizationHeader.substring(7);
            AuthResponse response = authService.refreshToken(refreshToken);
            logger.info("POST /api/auth/refresh - Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("POST /api/auth/refresh - Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("POST /api/auth/signout - User attempting to sign out");
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.warn("POST /api/auth/signout - Invalid authorization header");
                return ResponseEntity.badRequest().build();
            }
            
            String accessToken = authorizationHeader.substring(7);
            authService.signOut(accessToken);
            logger.info("POST /api/auth/signout - User signed out successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("POST /api/auth/signout - Sign out failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("GET /api/auth/test - Test endpoint called");
        return ResponseEntity.ok("Auth service is working!");
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("GET /api/auth/health - Health check called");
        return ResponseEntity.ok("Auth service is healthy!");
    }
    
    @GetMapping("/db-check")
    public ResponseEntity<String> dbCheck() {
        logger.info("GET /api/auth/db-check - Database check called");
        try {
            long userCount = loginRepository.count();
            logger.info("Database user count: {}", userCount);
            
            // Check if john.doe exists
            boolean johnExists = loginRepository.existsByUsername("john.doe");
            logger.info("User 'john.doe' exists: {}", johnExists);
            
            if (johnExists) {
                Login johnLogin = loginRepository.findByUsername("john.doe").orElse(null);
                if (johnLogin != null) {
                    logger.info("John's password hash: {}", johnLogin.getPassword());
                }
            }
            
            return ResponseEntity.ok("Database is working! User count: " + userCount + ", john.doe exists: " + johnExists);
        } catch (Exception e) {
            logger.error("Database check failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }
} 