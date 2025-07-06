package com.example.jwtuserservice.repository;

import com.example.jwtuserservice.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    
    Optional<Login> findByUsername(String username);
    
    boolean existsByUsername(String username);
} 