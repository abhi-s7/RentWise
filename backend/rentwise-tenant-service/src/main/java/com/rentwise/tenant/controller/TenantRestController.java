package com.rentwise.tenant.controller;

import com.rentwise.tenant.model.Tenant;
import com.rentwise.tenant.model.TenantRequest;
import com.rentwise.tenant.service.TenantService;
import com.rentwise.tenant.service.TenantRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantRestController.class);
    private static final String SERVICE_NAME = "rentwise-tenant-service";
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private TenantRequestService tenantRequestService;
    
    @GetMapping
    public List<Tenant> getAllTenants() {
        logger.info("[{}] [TenantRestController] [getAllTenants] START - API: GET /api/tenants", SERVICE_NAME);
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            logger.info("[{}] [TenantRestController] [getAllTenants] SUCCESS - Returning {} tenants", SERVICE_NAME, tenants.size());
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getAllTenants] ERROR - Failed to retrieve tenants: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        logger.info("[{}] [TenantRestController] [getTenantById] START - API: GET /api/tenants/{}", SERVICE_NAME, id);
        try {
            Tenant tenant = tenantService.getTenantById(id);
            if (tenant != null) {
                logger.info("[{}] [TenantRestController] [getTenantById] SUCCESS - Tenant found with ID: {}", SERVICE_NAME, id);
                return ResponseEntity.ok(tenant);
            }
            logger.warn("[{}] [TenantRestController] [getTenantById] Tenant not found with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getTenantById] ERROR - Failed to get tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody Tenant tenant) {
        logger.info("[{}] [TenantRestController] [createTenant] START - API: POST /api/tenants - Email: {}", 
                SERVICE_NAME, tenant.getEmail());
        try {
            Tenant created = tenantService.createTenant(tenant);
            logger.info("[{}] [TenantRestController] [createTenant] SUCCESS - Tenant created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [createTenant] ERROR - Failed to create tenant: {} - Error: {}", 
                    SERVICE_NAME, tenant.getEmail(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Long id, @RequestBody Tenant tenant) {
        logger.info("[{}] [TenantRestController] [updateTenant] START - API: PUT /api/tenants/{}", SERVICE_NAME, id);
        try {
            Tenant updated = tenantService.updateTenant(id, tenant);
            if (updated != null) {
                logger.info("[{}] [TenantRestController] [updateTenant] SUCCESS - Tenant updated with ID: {}", SERVICE_NAME, id);
                return ResponseEntity.ok(updated);
            }
            logger.warn("[{}] [TenantRestController] [updateTenant] Tenant not found with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [updateTenant] ERROR - Failed to update tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        logger.info("[{}] [TenantRestController] [deleteTenant] START - API: DELETE /api/tenants/{}", SERVICE_NAME, id);
        try {
            tenantService.deleteTenant(id);
            logger.info("[{}] [TenantRestController] [deleteTenant] SUCCESS - Tenant deleted with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [deleteTenant] ERROR - Failed to delete tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tenant>> getTenantsByUserId(@PathVariable Long userId) {
        logger.info("[{}] [TenantRestController] [getTenantsByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<Tenant> tenants = tenantService.getTenantsByUserId(userId);
            logger.info("[{}] [TenantRestController] [getTenantsByUserId] SUCCESS - Found {} tenants", 
                    SERVICE_NAME, tenants.size());
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getTenantsByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Tenant>> getTenantsByPropertyId(@PathVariable Long propertyId) {
        logger.info("[{}] [TenantRestController] [getTenantsByPropertyId] START - Property ID: {}", SERVICE_NAME, propertyId);
        try {
            List<Tenant> tenants = tenantService.getTenantsByPropertyId(propertyId);
            logger.info("[{}] [TenantRestController] [getTenantsByPropertyId] SUCCESS - Found {} tenants", 
                    SERVICE_NAME, tenants.size());
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getTenantsByPropertyId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Tenant Request endpoints
    @PostMapping("/requests")
    public ResponseEntity<?> createTenantRequest(@RequestBody TenantRequest request) {
        logger.info("[{}] [TenantRestController] [createTenantRequest] START - Email: {}", SERVICE_NAME, request.getEmail());
        try {
            TenantRequest created = tenantRequestService.createTenantRequest(request);
            logger.info("[{}] [TenantRestController] [createTenantRequest] SUCCESS - Request created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [createTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/requests")
    public ResponseEntity<List<TenantRequest>> getAllTenantRequests() {
        logger.info("[{}] [TenantRestController] [getAllTenantRequests] START", SERVICE_NAME);
        try {
            List<TenantRequest> requests = tenantRequestService.getAllTenantRequests();
            logger.info("[{}] [TenantRestController] [getAllTenantRequests] SUCCESS - Found {} requests", 
                    SERVICE_NAME, requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getAllTenantRequests] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<List<TenantRequest>> getTenantRequestsByUser(@PathVariable Long userId) {
        logger.info("[{}] [TenantRestController] [getTenantRequestsByUser] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantRequest> requests = tenantRequestService.getTenantRequestsByUser(userId);
            logger.info("[{}] [TenantRestController] [getTenantRequestsByUser] SUCCESS - Found {} requests", 
                    SERVICE_NAME, requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getTenantRequestsByUser] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/requests/pending")
    public ResponseEntity<List<TenantRequest>> getPendingTenantRequests() {
        logger.info("[{}] [TenantRestController] [getPendingTenantRequests] START", SERVICE_NAME);
        try {
            List<TenantRequest> requests = tenantRequestService.getPendingTenantRequests();
            logger.info("[{}] [TenantRestController] [getPendingTenantRequests] SUCCESS - Found {} pending requests", 
                    SERVICE_NAME, requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [getPendingTenantRequests] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/requests/{id}/approve")
    public ResponseEntity<?> approveTenantRequest(@PathVariable Long id) {
        logger.info("[{}] [TenantRestController] [approveTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            TenantRequest approved = tenantRequestService.approveTenantRequest(id);
            logger.info("[{}] [TenantRestController] [approveTenantRequest] SUCCESS - Request approved", SERVICE_NAME);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [approveTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<?> rejectTenantRequest(@PathVariable Long id) {
        logger.info("[{}] [TenantRestController] [rejectTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            TenantRequest rejected = tenantRequestService.rejectTenantRequest(id);
            logger.info("[{}] [TenantRestController] [rejectTenantRequest] SUCCESS - Request rejected", SERVICE_NAME);
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [rejectTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/assign-property")
    public ResponseEntity<?> assignPropertyToTenant(@PathVariable Long id, @RequestParam Long propertyId) {
        logger.info("[{}] [TenantRestController] [assignPropertyToTenant] START - Tenant ID: {}, Property ID: {}", 
                SERVICE_NAME, id, propertyId);
        try {
            Tenant updated = tenantService.assignPropertyToTenant(id, propertyId);
            logger.info("[{}] [TenantRestController] [assignPropertyToTenant] SUCCESS - Property assigned", SERVICE_NAME);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("[{}] [TenantRestController] [assignPropertyToTenant] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

