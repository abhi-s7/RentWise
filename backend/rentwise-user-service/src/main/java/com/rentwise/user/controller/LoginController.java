package com.rentwise.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String SERVICE_NAME = "rentwise-user-service";
    
    @GetMapping("/login")
    public String login() {
        logger.info("[{}] [LoginController] [login] Showing login page", SERVICE_NAME);
        return "login";
    }
}

