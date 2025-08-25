package com.rentwise.user.controller;

import com.rentwise.user.model.User;
import com.rentwise.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String SERVICE_NAME = "rentwise-user-service";
    
    @Autowired
    private UserService userService;
    
    // Thymeleaf Web Endpoints
    @GetMapping("/")
    public String index() {
        logger.info("[{}] [UserController] [index] Redirecting to /login", SERVICE_NAME);
        return "redirect:/login";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        logger.info("[{}] [UserController] [listUsers] START - API: GET /users", SERVICE_NAME);
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            logger.info("[{}] [UserController] [listUsers] SUCCESS - Retrieved {} users", SERVICE_NAME, users.size());
            return "users/list";
        } catch (Exception e) {
            logger.error("[{}] [UserController] [listUsers] ERROR - Failed to retrieve users: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/users/register")
    public String showRegisterForm(Model model) {
        logger.info("[{}] [UserController] [showRegisterForm] START - API: GET /users/register", SERVICE_NAME);
        model.addAttribute("user", new User());
        logger.debug("[{}] [UserController] [showRegisterForm] Registration form displayed", SERVICE_NAME);
        return "users/register";
    }
    
    @PostMapping("/users/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        logger.info("[{}] [UserController] [registerUser] START - API: POST /users/register - Username: {}", 
                SERVICE_NAME, user.getUsername());
        try {
            User savedUser = userService.registerUser(user);
            logger.info("[{}] [UserController] [registerUser] SUCCESS - User registered with ID: {}", 
                    SERVICE_NAME, savedUser.getId());
            return "redirect:/login?registered";
        } catch (Exception e) {
            logger.error("[{}] [UserController] [registerUser] ERROR - Registration failed for username: {} - Error: {}", 
                    SERVICE_NAME, user.getUsername(), e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "users/register";
        }
    }
    
    @GetMapping("/users/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        logger.info("[{}] [UserController] [getUserDetails] START - API: GET /users/{}", SERVICE_NAME, id);
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                model.addAttribute("user", user);
                logger.info("[{}] [UserController] [getUserDetails] SUCCESS - User details retrieved for ID: {}", 
                        SERVICE_NAME, id);
                return "users/details";
            }
            logger.warn("[{}] [UserController] [getUserDetails] User not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/users?error";
        } catch (Exception e) {
            logger.error("[{}] [UserController] [getUserDetails] ERROR - Failed to get user details for ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/users?error";
        }
    }
}


