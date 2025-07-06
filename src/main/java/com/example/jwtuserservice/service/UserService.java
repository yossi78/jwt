package com.example.jwtuserservice.service;

import com.example.jwtuserservice.dto.UserDto;
import com.example.jwtuserservice.entity.User;
import com.example.jwtuserservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDto> getUserById(Long id) {
        logger.info("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<UserDto> searchUsers(String searchTerm) {
        logger.info("Searching users with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        
        return userRepository.findByFirstNameOrLastNameContainingIgnoreCase(searchTerm.trim())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user: {}", userDto.getFirstName() + " " + userDto.getLastName());
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getId());
        return convertToDto(savedUser);
    }
    
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        logger.info("Updating user with id: {}", id);
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(userDto.getFirstName());
                    existingUser.setLastName(userDto.getLastName());
                    existingUser.setAge(userDto.getAge());
                    existingUser.setBirthday(userDto.getBirthday());
                    
                    User updatedUser = userRepository.save(existingUser);
                    logger.info("User updated successfully: {}", updatedUser.getId());
                    return convertToDto(updatedUser);
                });
    }
    
    public boolean deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User deleted successfully: {}", id);
            return true;
        }
        logger.warn("User not found for deletion: {}", id);
        return false;
    }
    
    public boolean deleteUsers(List<Long> ids) {
        logger.info("Deleting multiple users: {}", ids);
        List<User> usersToDelete = userRepository.findAllById(ids);
        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
            logger.info("Successfully deleted {} users", usersToDelete.size());
            return true;
        }
        logger.warn("No users found for deletion");
        return false;
    }
    
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getBirthday()
        );
    }
    
    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAge(userDto.getAge());
        user.setBirthday(userDto.getBirthday());
        return user;
    }
} 