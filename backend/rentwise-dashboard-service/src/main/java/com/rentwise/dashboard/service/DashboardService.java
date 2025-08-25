package com.rentwise.dashboard.service;

import com.rentwise.dashboard.client.PropertyServiceClient;
import com.rentwise.dashboard.client.TenantServiceClient;
import com.rentwise.dashboard.client.UserServiceClient;
import com.rentwise.dashboard.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private static final String SERVICE_NAME = "rentwise-dashboard-service";
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private PropertyServiceClient propertyServiceClient;
    
    @Autowired
    private TenantServiceClient tenantServiceClient;
    
    public List<PropertyDTO> getAllProperties() {
        logger.info("[{}] [DashboardService] [getAllProperties] START", SERVICE_NAME);
        try {
            List<PropertyDTO> properties = propertyServiceClient.getAllProperties();
            List<UserDTO> allUsers = userServiceClient.getAllUsers();
            
            // Enrich properties with owner names and tenant counts
            for (PropertyDTO property : properties) {
                if (property.getUserId() != null) {
                    UserDTO owner = allUsers.stream()
                            .filter(u -> u.getId().equals(property.getUserId()))
                            .findFirst()
                            .orElse(null);
                    if (owner != null) {
                        property.setOwnerName(owner.getUsername());
                    }
                }
                
                // Get tenant count for this property
                if (property.getId() != null) {
                    try {
                        List<TenantDTO> tenants = tenantServiceClient.getTenantsByPropertyId(property.getId());
                        property.setTenantCount(tenants.size());
                    } catch (Exception e) {
                        property.setTenantCount(0);
                    }
                }
            }
            
            logger.info("[{}] [DashboardService] [getAllProperties] SUCCESS - Retrieved {} properties", 
                    SERVICE_NAME, properties.size());
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getAllProperties] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantDTO> getAllTenants() {
        logger.info("[{}] [DashboardService] [getAllTenants] START", SERVICE_NAME);
        try {
            List<TenantDTO> tenants = tenantServiceClient.getAllTenants();
            if (tenants == null || tenants.isEmpty()) {
                logger.info("[{}] [DashboardService] [getAllTenants] No tenants found", SERVICE_NAME);
                return new java.util.ArrayList<>();
            }
            List<UserDTO> allUsers = userServiceClient.getAllUsers();
            List<PropertyDTO> allProperties = propertyServiceClient.getAllProperties();
            
            // Enrich tenants with user names and property names
            for (TenantDTO tenant : tenants) {
                if (tenant.getUserId() != null) {
                    UserDTO user = allUsers.stream()
                            .filter(u -> u.getId().equals(tenant.getUserId()))
                            .findFirst()
                            .orElse(null);
                    if (user != null) {
                        tenant.setRoommateOf(user.getUsername());
                    }
                }
                
                if (tenant.getPropertyId() != null) {
                    PropertyDTO property = allProperties.stream()
                            .filter(p -> p.getId().equals(tenant.getPropertyId()))
                            .findFirst()
                            .orElse(null);
                    if (property != null) {
                        tenant.setPropertyName(property.getName());
                    }
                }
            }
            
            logger.info("[{}] [DashboardService] [getAllTenants] SUCCESS - Retrieved {} tenants", 
                    SERVICE_NAME, tenants.size());
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getAllTenants] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantDTO> getTenantsByUserId(Long userId) {
        logger.info("[{}] [DashboardService] [getTenantsByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantDTO> tenants = tenantServiceClient.getTenantsByUserId(userId);
            List<PropertyDTO> allProperties = propertyServiceClient.getAllProperties();
            
            // Enrich tenants with property names
            for (TenantDTO tenant : tenants) {
                if (tenant.getPropertyId() != null) {
                    PropertyDTO property = allProperties.stream()
                            .filter(p -> p.getId() != null && p.getId().equals(tenant.getPropertyId()))
                            .findFirst()
                            .orElse(null);
                    if (property != null && property.getName() != null) {
                        tenant.setPropertyName(property.getName());
                        logger.debug("[{}] [DashboardService] [getTenantsByUserId] Set property name '{}' for tenant {}", 
                                SERVICE_NAME, property.getName(), tenant.getId());
                    } else {
                        logger.warn("[{}] [DashboardService] [getTenantsByUserId] Property not found for propertyId: {}", 
                                SERVICE_NAME, tenant.getPropertyId());
                        tenant.setPropertyName("Property ID: " + tenant.getPropertyId());
                    }
                } else {
                    tenant.setPropertyName(null);
                }
            }
            
            logger.info("[{}] [DashboardService] [getTenantsByUserId] SUCCESS - Retrieved {} tenants for user {}", 
                    SERVICE_NAME, tenants.size(), userId);
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getTenantsByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<PropertyDTO> getPropertiesByUserId(Long userId) {
        logger.info("[{}] [DashboardService] [getPropertiesByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            // Get properties where user is the owner
            List<PropertyDTO> ownedProperties = propertyServiceClient.getPropertiesByUserId(userId);
            
            // Get tenants where this user is the roommate owner (to find properties where user's roommates are assigned)
            List<TenantDTO> userTenants = tenantServiceClient.getTenantsByUserId(userId);
            
            // Get all properties to find ones where user's tenants are assigned
            List<PropertyDTO> allProperties = propertyServiceClient.getAllProperties();
            
            // Collect unique property IDs from user's tenants
            java.util.Set<Long> propertyIds = new java.util.HashSet<>();
            for (TenantDTO tenant : userTenants) {
                if (tenant.getPropertyId() != null) {
                    propertyIds.add(tenant.getPropertyId());
                }
            }
            
            // Get properties where user's tenants are assigned
            List<PropertyDTO> tenantProperties = new java.util.ArrayList<>();
            for (Long propertyId : propertyIds) {
                PropertyDTO property = allProperties.stream()
                        .filter(p -> p.getId().equals(propertyId))
                        .findFirst()
                        .orElse(null);
                if (property != null) {
                    tenantProperties.add(property);
                }
            }
            
            // Combine owned properties and tenant properties (avoid duplicates)
            java.util.Map<Long, PropertyDTO> uniqueProperties = new java.util.HashMap<>();
            for (PropertyDTO prop : ownedProperties) {
                uniqueProperties.put(prop.getId(), prop);
            }
            for (PropertyDTO prop : tenantProperties) {
                if (!uniqueProperties.containsKey(prop.getId())) {
                    uniqueProperties.put(prop.getId(), prop);
                }
            }
            
            List<PropertyDTO> properties = new java.util.ArrayList<>(uniqueProperties.values());
            
            // Enrich properties with tenant counts
            for (PropertyDTO property : properties) {
                if (property.getId() != null) {
                    try {
                        List<TenantDTO> tenants = tenantServiceClient.getTenantsByPropertyId(property.getId());
                        property.setTenantCount(tenants.size());
                    } catch (Exception e) {
                        property.setTenantCount(0);
                    }
                }
            }
            
            logger.info("[{}] [DashboardService] [getPropertiesByUserId] SUCCESS - Retrieved {} properties for user {}", 
                    SERVICE_NAME, properties.size(), userId);
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getPropertiesByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantRequestDTO> getPendingTenantRequests() {
        logger.info("[{}] [DashboardService] [getPendingTenantRequests] START", SERVICE_NAME);
        try {
            List<TenantRequestDTO> requests = tenantServiceClient.getPendingTenantRequests();
            logger.info("[{}] [DashboardService] [getPendingTenantRequests] SUCCESS - Retrieved {} pending requests", 
                    SERVICE_NAME, requests.size());
            return requests;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getPendingTenantRequests] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantRequestDTO> getTenantRequestsByUser(Long userId) {
        logger.info("[{}] [DashboardService] [getTenantRequestsByUser] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantRequestDTO> requests = tenantServiceClient.getTenantRequestsByUser(userId);
            logger.info("[{}] [DashboardService] [getTenantRequestsByUser] SUCCESS - Retrieved {} requests for user {}", 
                    SERVICE_NAME, requests.size(), userId);
            return requests;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [getTenantRequestsByUser] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantRequestDTO createTenantRequest(TenantRequestDTO request) {
        logger.info("[{}] [DashboardService] [createTenantRequest] START - Email: {}", SERVICE_NAME, request.getEmail());
        try {
            TenantRequestDTO created = tenantServiceClient.createTenantRequest(request);
            logger.info("[{}] [DashboardService] [createTenantRequest] SUCCESS - Request created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return created;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [createTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantRequestDTO approveTenantRequest(Long requestId) {
        logger.info("[{}] [DashboardService] [approveTenantRequest] START - Request ID: {}", SERVICE_NAME, requestId);
        try {
            TenantRequestDTO approved = tenantServiceClient.approveTenantRequest(requestId);
            logger.info("[{}] [DashboardService] [approveTenantRequest] SUCCESS - Request approved with ID: {}", 
                    SERVICE_NAME, requestId);
            return approved;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [approveTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantRequestDTO rejectTenantRequest(Long requestId) {
        logger.info("[{}] [DashboardService] [rejectTenantRequest] START - Request ID: {}", SERVICE_NAME, requestId);
        try {
            TenantRequestDTO rejected = tenantServiceClient.rejectTenantRequest(requestId);
            logger.info("[{}] [DashboardService] [rejectTenantRequest] SUCCESS - Request rejected with ID: {}", 
                    SERVICE_NAME, requestId);
            return rejected;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [rejectTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantDTO assignPropertyToTenant(Long tenantId, Long propertyId) {
        logger.info("[{}] [DashboardService] [assignPropertyToTenant] START - Tenant ID: {}, Property ID: {}", 
                SERVICE_NAME, tenantId, propertyId);
        try {
            TenantDTO updated = tenantServiceClient.assignPropertyToTenant(tenantId, propertyId);
            logger.info("[{}] [DashboardService] [assignPropertyToTenant] SUCCESS", SERVICE_NAME);
            return updated;
        } catch (Exception e) {
            logger.error("[{}] [DashboardService] [assignPropertyToTenant] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
}

