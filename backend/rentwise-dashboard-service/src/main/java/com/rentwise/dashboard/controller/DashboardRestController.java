package com.rentwise.dashboard.controller;

import com.rentwise.dashboard.dto.PropertyDTO;
import com.rentwise.dashboard.dto.TenantDTO;
import com.rentwise.dashboard.dto.TenantRequestDTO;
import com.rentwise.dashboard.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardRestController.class);
    private static final String SERVICE_NAME = "rentwise-dashboard-service";
    
    @Autowired
    private DashboardService dashboardService;
    
    // Admin Dashboard Endpoints
    @GetMapping("/admin/properties")
    public ResponseEntity<List<PropertyDTO>> getAdminProperties() {
        logger.info("[{}] [DashboardRestController] [getAdminProperties] START", SERVICE_NAME);
        try {
            List<PropertyDTO> properties = dashboardService.getAllProperties();
            logger.info("[{}] [DashboardRestController] [getAdminProperties] SUCCESS - Retrieved {} properties", 
                    SERVICE_NAME, properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getAdminProperties] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/admin/tenants")
    public ResponseEntity<List<TenantDTO>> getAdminTenants() {
        logger.info("[{}] [DashboardRestController] [getAdminTenants] START", SERVICE_NAME);
        try {
            List<TenantDTO> tenants = dashboardService.getAllTenants();
            logger.info("[{}] [DashboardRestController] [getAdminTenants] SUCCESS - Retrieved {} tenants", 
                    SERVICE_NAME, tenants.size());
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getAdminTenants] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/admin/pending-requests")
    public ResponseEntity<List<TenantRequestDTO>> getPendingRequests() {
        logger.info("[{}] [DashboardRestController] [getPendingRequests] START", SERVICE_NAME);
        try {
            List<TenantRequestDTO> requests = dashboardService.getPendingTenantRequests();
            logger.info("[{}] [DashboardRestController] [getPendingRequests] SUCCESS - Retrieved {} requests", 
                    SERVICE_NAME, requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getPendingRequests] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // User Dashboard Endpoints
    @GetMapping("/user/tenants")
    public ResponseEntity<List<TenantDTO>> getUserTenants(@RequestParam Long userId) {
        logger.info("[{}] [DashboardRestController] [getUserTenants] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantDTO> tenants = dashboardService.getTenantsByUserId(userId);
            logger.info("[{}] [DashboardRestController] [getUserTenants] SUCCESS - Retrieved {} tenants", 
                    SERVICE_NAME, tenants.size());
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getUserTenants] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/requests")
    public ResponseEntity<List<TenantRequestDTO>> getUserRequests(@RequestParam Long userId) {
        logger.info("[{}] [DashboardRestController] [getUserRequests] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantRequestDTO> requests = dashboardService.getTenantRequestsByUser(userId);
            logger.info("[{}] [DashboardRestController] [getUserRequests] SUCCESS - Retrieved {} requests", 
                    SERVICE_NAME, requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getUserRequests] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/properties")
    public ResponseEntity<List<PropertyDTO>> getUserProperties(@RequestParam Long userId) {
        logger.info("[{}] [DashboardRestController] [getUserProperties] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<PropertyDTO> properties = dashboardService.getPropertiesByUserId(userId);
            logger.info("[{}] [DashboardRestController] [getUserProperties] SUCCESS - Retrieved {} properties", 
                    SERVICE_NAME, properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [getUserProperties] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Action Endpoints
    @PostMapping("/user/tenant-request")
    public ResponseEntity<TenantRequestDTO> createTenantRequest(@RequestBody TenantRequestDTO request) {
        logger.info("[{}] [DashboardRestController] [createTenantRequest] START - Email: {}", 
                SERVICE_NAME, request.getEmail());
        try {
            TenantRequestDTO created = dashboardService.createTenantRequest(request);
            logger.info("[{}] [DashboardRestController] [createTenantRequest] SUCCESS - Request ID: {}", 
                    SERVICE_NAME, created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [createTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/admin/tenant-requests/{id}/approve")
    public ResponseEntity<TenantRequestDTO> approveTenantRequest(@PathVariable Long id) {
        logger.info("[{}] [DashboardRestController] [approveTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            TenantRequestDTO approved = dashboardService.approveTenantRequest(id);
            logger.info("[{}] [DashboardRestController] [approveTenantRequest] SUCCESS", SERVICE_NAME);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [approveTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/admin/tenant-requests/{id}/reject")
    public ResponseEntity<TenantRequestDTO> rejectTenantRequest(@PathVariable Long id) {
        logger.info("[{}] [DashboardRestController] [rejectTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            TenantRequestDTO rejected = dashboardService.rejectTenantRequest(id);
            logger.info("[{}] [DashboardRestController] [rejectTenantRequest] SUCCESS", SERVICE_NAME);
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [rejectTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/admin/tenants/{id}/assign-property")
    public ResponseEntity<TenantDTO> assignPropertyToTenant(
            @PathVariable Long id,
            @RequestParam Long propertyId) {
        logger.info("[{}] [DashboardRestController] [assignPropertyToTenant] START - Tenant ID: {}, Property ID: {}", 
                SERVICE_NAME, id, propertyId);
        try {
            TenantDTO updated = dashboardService.assignPropertyToTenant(id, propertyId);
            logger.info("[{}] [DashboardRestController] [assignPropertyToTenant] SUCCESS", SERVICE_NAME);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("[{}] [DashboardRestController] [assignPropertyToTenant] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

