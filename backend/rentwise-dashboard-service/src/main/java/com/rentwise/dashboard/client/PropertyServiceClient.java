package com.rentwise.dashboard.client;

import com.rentwise.dashboard.dto.PropertyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "rentwise-property-service")
public interface PropertyServiceClient {
    
    @GetMapping("/api/properties")
    List<PropertyDTO> getAllProperties();
    
    @GetMapping("/api/properties/{id}")
    PropertyDTO getPropertyById(@PathVariable Long id);
    
    @GetMapping("/api/properties/user/{userId}")
    List<PropertyDTO> getPropertiesByUserId(@PathVariable Long userId);
}

