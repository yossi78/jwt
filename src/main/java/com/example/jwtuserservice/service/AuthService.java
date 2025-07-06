package com.example.jwtuserservice.service;

import com.example.jwtuserservice.dto.AuthRequest;
import com.example.jwtuserservice.dto.AuthResponse;
import com.example.jwtuserservice.entity.Login;
import com.example.jwtuserservice.entity.User;
import com.example.jwtuserservice.repository.LoginRepository;
import com.example.jwtuserservice.repository.UserRepository;
import com.example.jwtuserservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private LoginRepository loginRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public AuthResponse authenticate(AuthRequest authRequest) {
        logger.info("Authenticating user: {}", authRequest.getUsername());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            final String accessToken = jwtUtil.generateAccessToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            logger.info("Authentication successful for user: {}", authRequest.getUsername());
            
            return new AuthResponse(accessToken, refreshToken, expiration);
            
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    public AuthResponse signUp(AuthRequest authRequest) {
        logger.info("Signing up new user: {}", authRequest.getUsername());
        
        if (loginRepository.existsByUsername(authRequest.getUsername())) {
            logger.warn("Username already exists: {}", authRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Create a new user
        User user = new User();
        user.setFirstName("New");
        user.setLastName("User");
        user.setAge(25);
        user.setBirthday(new Date());
        
        User savedUser = userRepository.save(user);
        
        // Create login credentials
        Login login = new Login();
        login.setUsername(authRequest.getUsername());
        login.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        login.setUser(savedUser);
        
        loginRepository.save(login);
        
        // Generate tokens
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String accessToken = jwtUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        logger.info("User signed up successfully: {}", authRequest.getUsername());
        
        return new AuthResponse(accessToken, refreshToken, expiration);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing token");
        
        try {
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                final String newAccessToken = jwtUtil.generateAccessToken(userDetails);
                final String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
                
                logger.info("Token refreshed successfully for user: {}", username);
                
                return new AuthResponse(newAccessToken, newRefreshToken, expiration);
            } else {
                throw new RuntimeException("Invalid refresh token");
            }
            
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new RuntimeException("Invalid refresh token");
        }
    }
} 