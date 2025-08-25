package com.rentwise.tenant.controller;

import com.rentwise.tenant.model.Tenant;
import com.rentwise.tenant.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    private static final String SERVICE_NAME = "rentwise-tenant-service";
    
    @Autowired
    private TenantService tenantService;
    
    // Thymeleaf Web Endpoints
    @GetMapping("/")
    public String index() {
        logger.info("[{}] [TenantController] [index] Redirecting to /tenants", SERVICE_NAME);
        return "redirect:/tenants";
    }
    
    @GetMapping("/tenants")
    public String listTenants(Model model) {
        logger.info("[{}] [TenantController] [listTenants] START - API: GET /tenants", SERVICE_NAME);
        try {
            model.addAttribute("tenants", tenantService.getAllTenants());
            logger.info("[{}] [TenantController] [listTenants] SUCCESS - Retrieved tenants list", SERVICE_NAME);
            return "tenants/list";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [listTenants] ERROR - Failed to retrieve tenants: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/tenants/new")
    public String showCreateForm(Model model) {
        logger.info("[{}] [TenantController] [showCreateForm] START - API: GET /tenants/new", SERVICE_NAME);
        model.addAttribute("tenant", new Tenant());
        logger.debug("[{}] [TenantController] [showCreateForm] Tenant creation form displayed", SERVICE_NAME);
        return "tenants/form";
    }
    
    @PostMapping("/tenants")
    public String createTenant(@ModelAttribute Tenant tenant, Model model) {
        logger.info("[{}] [TenantController] [createTenant] START - API: POST /tenants - Email: {}", 
                SERVICE_NAME, tenant.getEmail());
        try {
            Tenant created = tenantService.createTenant(tenant);
            logger.info("[{}] [TenantController] [createTenant] SUCCESS - Tenant created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return "redirect:/tenants?success";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [createTenant] ERROR - Failed to create tenant: {} - Error: {}", 
                    SERVICE_NAME, tenant.getEmail(), e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tenant", tenant);
            return "tenants/form";
        }
    }
    
    @GetMapping("/tenants/{id}")
    public String getTenantDetails(@PathVariable Long id, Model model) {
        logger.info("[{}] [TenantController] [getTenantDetails] START - API: GET /tenants/{}", SERVICE_NAME, id);
        try {
            Tenant tenant = tenantService.getTenantById(id);
            if (tenant != null) {
                model.addAttribute("tenant", tenant);
                logger.info("[{}] [TenantController] [getTenantDetails] SUCCESS - Tenant details retrieved for ID: {}", 
                        SERVICE_NAME, id);
                return "tenants/details";
            }
            logger.warn("[{}] [TenantController] [getTenantDetails] Tenant not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/tenants?error";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [getTenantDetails] ERROR - Failed to get tenant details for ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/tenants?error";
        }
    }
    
    @GetMapping("/tenants/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("[{}] [TenantController] [showEditForm] START - API: GET /tenants/{}/edit", SERVICE_NAME, id);
        try {
            Tenant tenant = tenantService.getTenantById(id);
            if (tenant != null) {
                model.addAttribute("tenant", tenant);
                logger.info("[{}] [TenantController] [showEditForm] SUCCESS - Edit form displayed for tenant ID: {}", 
                        SERVICE_NAME, id);
                return "tenants/form";
            }
            logger.warn("[{}] [TenantController] [showEditForm] Tenant not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/tenants?error";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [showEditForm] ERROR - Failed to show edit form for ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/tenants?error";
        }
    }
    
    @PostMapping("/tenants/{id}")
    public String updateTenant(@PathVariable Long id, @ModelAttribute Tenant tenant, Model model) {
        logger.info("[{}] [TenantController] [updateTenant] START - API: POST /tenants/{}", SERVICE_NAME, id);
        try {
            Tenant updated = tenantService.updateTenant(id, tenant);
            if (updated != null) {
                logger.info("[{}] [TenantController] [updateTenant] SUCCESS - Tenant updated with ID: {}", SERVICE_NAME, id);
                return "redirect:/tenants?success";
            }
            logger.warn("[{}] [TenantController] [updateTenant] Tenant not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/tenants?error";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [updateTenant] ERROR - Failed to update tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tenant", tenant);
            return "tenants/form";
        }
    }
    
    @PostMapping("/tenants/{id}/delete")
    public String deleteTenant(@PathVariable Long id) {
        logger.info("[{}] [TenantController] [deleteTenant] START - API: POST /tenants/{}/delete", SERVICE_NAME, id);
        try {
            tenantService.deleteTenant(id);
            logger.info("[{}] [TenantController] [deleteTenant] SUCCESS - Tenant deleted with ID: {}", SERVICE_NAME, id);
            return "redirect:/tenants?success";
        } catch (Exception e) {
            logger.error("[{}] [TenantController] [deleteTenant] ERROR - Failed to delete tenant with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/tenants?error";
        }
    }
}

