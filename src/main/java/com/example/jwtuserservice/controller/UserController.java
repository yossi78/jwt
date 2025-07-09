package com.example.jwtuserservice.controller;

import com.example.jwtuserservice.dto.UserDto;
import com.example.jwtuserservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // Test
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        List<UserDto> users = userService.getAllUsers();
        logger.info("GET /api/users - Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Fetching user by id", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("GET /api/users/{} - User found", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/users/{} - User not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String q) {
        logger.info("GET /api/users/search?q={} - Searching users", q);
        List<UserDto> users = userService.searchUsers(q);
        logger.info("GET /api/users/search - Found {} users matching criteria", users.size());
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("POST /api/users - Creating new user: {} {}", userDto.getFirstName(), userDto.getLastName());
        try {
            UserDto createdUser = userService.createUser(userDto);
            logger.info("POST /api/users - User created successfully with id: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("POST /api/users - Error creating user", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        logger.info("PUT /api/users/{} - Updating user", id);
        return userService.updateUser(id, userDto)
                .map(updatedUser -> {
                    logger.info("PUT /api/users/{} - User updated successfully", id);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> {
                    logger.warn("PUT /api/users/{} - User not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            logger.info("DELETE /api/users/{} - User deleted successfully", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("DELETE /api/users/{} - User not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteUsers(@RequestBody List<Long> ids) {
        logger.info("DELETE /api/users/batch - Deleting {} users", ids.size());
        boolean deleted = userService.deleteUsers(ids);
        if (deleted) {
            logger.info("DELETE /api/users/batch - Users deleted successfully");
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("DELETE /api/users/batch - No users found for deletion");
            return ResponseEntity.notFound().build();
        }
    }
} 