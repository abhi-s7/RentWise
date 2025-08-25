package com.rentwise.user.service;

import com.rentwise.user.model.User;
import com.rentwise.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String SERVICE_NAME = "rentwise-user-service";
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        logger.info("[{}] [UserService] [Constructor] UserService initialized successfully", SERVICE_NAME);
    }
    
    public User registerUser(User user) throws Exception {
        logger.info("[{}] [UserService] [registerUser] START - Registering user with username: {}", SERVICE_NAME, user.getUsername());
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("[{}] [UserService] [registerUser] Username already exists: {}", SERVICE_NAME, user.getUsername());
                throw new Exception("Username already exists");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                logger.warn("[{}] [UserService] [registerUser] Email already exists: {}", SERVICE_NAME, user.getEmail());
                throw new Exception("Email already exists");
            }
            
            logger.debug("[{}] [UserService] [registerUser] Encoding password for user: {}", SERVICE_NAME, user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            User savedUser = userRepository.save(user);
            logger.info("[{}] [UserService] [registerUser] SUCCESS - User registered successfully with ID: {}", SERVICE_NAME, savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("[{}] [UserService] [registerUser] ERROR - Failed to register user: {} - Error: {}", 
                    SERVICE_NAME, user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
    
    public User findByUsername(String username) {
        logger.debug("[{}] [UserService] [findByUsername] Searching for user with username: {}", SERVICE_NAME, username);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            logger.debug("[{}] [UserService] [findByUsername] User found with ID: {}", SERVICE_NAME, user.getId());
        } else {
            logger.debug("[{}] [UserService] [findByUsername] User not found with username: {}", SERVICE_NAME, username);
        }
        return user;
    }
    
    public User findByEmail(String email) {
        logger.debug("[{}] [UserService] [findByEmail] Searching for user with email: {}", SERVICE_NAME, email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            logger.debug("[{}] [UserService] [findByEmail] User found with ID: {}", SERVICE_NAME, user.getId());
        } else {
            logger.debug("[{}] [UserService] [findByEmail] User not found with email: {}", SERVICE_NAME, email);
        }
        return user;
    }
    
    public User getUserById(Long id) {
        logger.info("[{}] [UserService] [getUserById] START - Fetching user with ID: {}", SERVICE_NAME, id);
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            logger.info("[{}] [UserService] [getUserById] SUCCESS - User found with ID: {}", SERVICE_NAME, id);
        } else {
            logger.warn("[{}] [UserService] [getUserById] User not found with ID: {}", SERVICE_NAME, id);
        }
        return user;
    }
    
    public List<User> getAllUsers() {
        logger.info("[{}] [UserService] [getAllUsers] START - Fetching all users", SERVICE_NAME);
        List<User> users = userRepository.findAll();
        logger.info("[{}] [UserService] [getAllUsers] SUCCESS - Found {} users", SERVICE_NAME, users.size());
        return users;
    }
    
    public void save(User user) {
        logger.info("[{}] [UserService] [save] START - Saving user with ID: {}", SERVICE_NAME, user.getId());
        try {
            userRepository.save(user);
            logger.info("[{}] [UserService] [save] SUCCESS - User saved with ID: {}", SERVICE_NAME, user.getId());
        } catch (Exception e) {
            logger.error("[{}] [UserService] [save] ERROR - Failed to save user with ID: {} - Error: {}", 
                    SERVICE_NAME, user.getId(), e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("[{}] [UserService] [loadUserByUsername] START - Loading user details for username: {}", SERVICE_NAME, username);
        try {
            User user = findByUsername(username);
            if (user == null) {
                logger.debug("[{}] [UserService] [loadUserByUsername] Username not found, trying email: {}", SERVICE_NAME, username);
                user = findByEmail(username);
                if (user == null) {
                    logger.warn("[{}] [UserService] [loadUserByUsername] User not found with username/email: {}", SERVICE_NAME, username);
                    throw new UsernameNotFoundException("User not found with username: " + username);
                }
            }
            logger.info("[{}] [UserService] [loadUserByUsername] SUCCESS - User details loaded for: {}", SERVICE_NAME, username);
            return user;
        } catch (UsernameNotFoundException e) {
            logger.error("[{}] [UserService] [loadUserByUsername] ERROR - {}", SERVICE_NAME, e.getMessage());
            throw e;
        }
    }
}

