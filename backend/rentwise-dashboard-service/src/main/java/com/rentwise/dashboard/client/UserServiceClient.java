package com.rentwise.dashboard.client;

import com.rentwise.dashboard.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "rentwise-user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users")
    List<UserDTO> getAllUsers();
    
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @GetMapping("/api/users/username/{username}")
    UserDTO getUserByUsername(@PathVariable String username);
}

