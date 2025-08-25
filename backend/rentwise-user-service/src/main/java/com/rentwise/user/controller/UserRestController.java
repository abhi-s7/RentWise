package com.rentwise.user.controller;

import com.rentwise.user.model.User;
import com.rentwise.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private static final String SERVICE_NAME = "rentwise-user-service";
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public List<User> getAllUsers() {
        logger.info("[{}] [UserRestController] [getAllUsers] START - API: GET /api/users", SERVICE_NAME);
        try {
            List<User> users = userService.getAllUsers();
            logger.info("[{}] [UserRestController] [getAllUsers] SUCCESS - Returning {} users", SERVICE_NAME, users.size());
            return users;
        } catch (Exception e) {
            logger.error("[{}] [UserRestController] [getAllUsers] ERROR - Failed to retrieve users: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("[{}] [UserRestController] [getUserById] START - API: GET /api/users/{}", SERVICE_NAME, id);
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                logger.info("[{}] [UserRestController] [getUserById] SUCCESS - User found with ID: {}", SERVICE_NAME, id);
                return ResponseEntity.ok(user);
            }
            logger.warn("[{}] [UserRestController] [getUserById] User not found with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [UserRestController] [getUserById] ERROR - Failed to get user with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("[{}] [UserRestController] [registerUser] START - API: POST /api/users/register - Username: {}", 
                SERVICE_NAME, user.getUsername());
        try {
            User savedUser = userService.registerUser(user);
            logger.info("[{}] [UserRestController] [registerUser] SUCCESS - User registered with ID: {}", 
                    SERVICE_NAME, savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("[{}] [UserRestController] [registerUser] ERROR - Registration failed for username: {} - Error: {}", 
                    SERVICE_NAME, user.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("[{}] [UserRestController] [getUserByUsername] START - Username: {}", SERVICE_NAME, username);
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                logger.info("[{}] [UserRestController] [getUserByUsername] SUCCESS - User found", SERVICE_NAME);
                return ResponseEntity.ok(user);
            }
            logger.warn("[{}] [UserRestController] [getUserByUsername] User not found with username: {}", SERVICE_NAME, username);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [UserRestController] [getUserByUsername] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

