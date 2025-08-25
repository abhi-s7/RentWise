package com.rentwise.tenant.service;

import com.rentwise.tenant.model.Tenant;
import com.rentwise.tenant.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);
    private static final String SERVICE_NAME = "rentwise-tenant-service";
    
    @Autowired
    private TenantRepository tenantRepository;
    
    public List<Tenant> getAllTenants() {
        logger.info("[{}] [TenantService] [getAllTenants] START - Fetching all tenants", SERVICE_NAME);
        try {
            List<Tenant> tenants = tenantRepository.findAll();
            logger.info("[{}] [TenantService] [getAllTenants] SUCCESS - Found {} tenants", SERVICE_NAME, tenants.size());
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [getAllTenants] ERROR - Failed to fetch tenants: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public Tenant getTenantById(Long id) {
        logger.info("[{}] [TenantService] [getTenantById] START - Fetching tenant with ID: {}", SERVICE_NAME, id);
        try {
            Tenant tenant = tenantRepository.findById(id).orElse(null);
            if (tenant != null) {
                logger.info("[{}] [TenantService] [getTenantById] SUCCESS - Tenant found with ID: {}", SERVICE_NAME, id);
            } else {
                logger.warn("[{}] [TenantService] [getTenantById] Tenant not found with ID: {}", SERVICE_NAME, id);
            }
            return tenant;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [getTenantById] ERROR - Failed to fetch tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public Tenant createTenant(Tenant tenant) throws Exception {
        logger.info("[{}] [TenantService] [createTenant] START - Creating tenant with email: {}", SERVICE_NAME, tenant.getEmail());
        try {
            if (tenantRepository.existsByEmail(tenant.getEmail())) {
                logger.warn("[{}] [TenantService] [createTenant] Email already exists: {}", SERVICE_NAME, tenant.getEmail());
                throw new Exception("Email already exists");
            }
            Tenant savedTenant = tenantRepository.save(tenant);
            logger.info("[{}] [TenantService] [createTenant] SUCCESS - Tenant created with ID: {}", SERVICE_NAME, savedTenant.getId());
            return savedTenant;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [createTenant] ERROR - Failed to create tenant: {} - Error: {}", 
                    SERVICE_NAME, tenant.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
    
    public Tenant updateTenant(Long id, Tenant tenant) {
        logger.info("[{}] [TenantService] [updateTenant] START - Updating tenant with ID: {}", SERVICE_NAME, id);
        try {
            Tenant existing = tenantRepository.findById(id).orElse(null);
            if (existing != null) {
                tenant.setId(id);
                Tenant updated = tenantRepository.save(tenant);
                logger.info("[{}] [TenantService] [updateTenant] SUCCESS - Tenant updated with ID: {}", SERVICE_NAME, id);
                return updated;
            }
            logger.warn("[{}] [TenantService] [updateTenant] Tenant not found with ID: {}", SERVICE_NAME, id);
            return null;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [updateTenant] ERROR - Failed to update tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public void deleteTenant(Long id) {
        logger.info("[{}] [TenantService] [deleteTenant] START - Deleting tenant with ID: {}", SERVICE_NAME, id);
        try {
            tenantRepository.deleteById(id);
            logger.info("[{}] [TenantService] [deleteTenant] SUCCESS - Tenant deleted with ID: {}", SERVICE_NAME, id);
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [deleteTenant] ERROR - Failed to delete tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Tenant> getTenantsByUserId(Long userId) {
        logger.info("[{}] [TenantService] [getTenantsByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<Tenant> tenants = tenantRepository.findByUserId(userId);
            logger.info("[{}] [TenantService] [getTenantsByUserId] SUCCESS - Found {} tenants for user {}", 
                    SERVICE_NAME, tenants.size(), userId);
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [getTenantsByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Tenant> getTenantsByPropertyId(Long propertyId) {
        logger.info("[{}] [TenantService] [getTenantsByPropertyId] START - Property ID: {}", SERVICE_NAME, propertyId);
        try {
            List<Tenant> tenants = tenantRepository.findByPropertyId(propertyId);
            logger.info("[{}] [TenantService] [getTenantsByPropertyId] SUCCESS - Found {} tenants for property {}", 
                    SERVICE_NAME, tenants.size(), propertyId);
            return tenants;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [getTenantsByPropertyId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public Tenant assignPropertyToTenant(Long tenantId, Long propertyId) throws Exception {
        logger.info("[{}] [TenantService] [assignPropertyToTenant] START - Tenant ID: {}, Property ID: {}", 
                SERVICE_NAME, tenantId, propertyId);
        try {
            Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
            if (tenant == null) {
                logger.warn("[{}] [TenantService] [assignPropertyToTenant] Tenant not found with ID: {}", SERVICE_NAME, tenantId);
                throw new Exception("Tenant not found with ID: " + tenantId);
            }
            tenant.setPropertyId(propertyId);
            Tenant updated = tenantRepository.save(tenant);
            logger.info("[{}] [TenantService] [assignPropertyToTenant] SUCCESS - Property {} assigned to tenant {}", 
                    SERVICE_NAME, propertyId, tenantId);
            return updated;
        } catch (Exception e) {
            logger.error("[{}] [TenantService] [assignPropertyToTenant] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
}

