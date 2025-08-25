package com.rentwise.dashboard.client;

import com.rentwise.dashboard.dto.TenantDTO;
import com.rentwise.dashboard.dto.TenantRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "rentwise-tenant-service")
public interface TenantServiceClient {
    
    @GetMapping("/api/tenants")
    List<TenantDTO> getAllTenants();
    
    @GetMapping("/api/tenants/{id}")
    TenantDTO getTenantById(@PathVariable Long id);
    
    @PostMapping("/api/tenants")
    TenantDTO createTenant(@RequestBody TenantDTO tenant);
    
    @PutMapping("/api/tenants/{id}")
    TenantDTO updateTenant(@PathVariable Long id, @RequestBody TenantDTO tenant);
    
    @DeleteMapping("/api/tenants/{id}")
    void deleteTenant(@PathVariable Long id);
    
    // Tenant Request endpoints
    @PostMapping("/api/tenants/requests")
    TenantRequestDTO createTenantRequest(@RequestBody TenantRequestDTO request);
    
    @GetMapping("/api/tenants/requests")
    List<TenantRequestDTO> getAllTenantRequests();
    
    @GetMapping("/api/tenants/requests/user/{userId}")
    List<TenantRequestDTO> getTenantRequestsByUser(@PathVariable Long userId);
    
    @GetMapping("/api/tenants/requests/pending")
    List<TenantRequestDTO> getPendingTenantRequests();
    
    @PutMapping("/api/tenants/requests/{id}/approve")
    TenantRequestDTO approveTenantRequest(@PathVariable Long id);
    
    @PutMapping("/api/tenants/requests/{id}/reject")
    TenantRequestDTO rejectTenantRequest(@PathVariable Long id);
    
    @GetMapping("/api/tenants/user/{userId}")
    List<TenantDTO> getTenantsByUserId(@PathVariable Long userId);
    
    @GetMapping("/api/tenants/property/{propertyId}")
    List<TenantDTO> getTenantsByPropertyId(@PathVariable Long propertyId);
    
    @PutMapping("/api/tenants/{id}/assign-property")
    TenantDTO assignPropertyToTenant(@PathVariable Long id, @RequestParam Long propertyId);
}

