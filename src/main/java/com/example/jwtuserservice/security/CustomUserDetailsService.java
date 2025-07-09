package com.example.jwtuserservice.security;

import com.example.jwtuserservice.entity.Login;
import com.example.jwtuserservice.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private LoginRepository loginRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);
        
        try {
            Login login = loginRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            
            logger.info("User found in database: {}", username);
            logger.debug("User password hash: {}", login.getPassword());
            
            return new User(login.getUsername(), login.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("USER")));
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }
} 