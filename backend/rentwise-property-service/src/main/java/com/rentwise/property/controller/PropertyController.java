package com.rentwise.property.controller;

import com.rentwise.property.model.Property;
import com.rentwise.property.service.PropertyService;
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
public class PropertyController {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);
    private static final String SERVICE_NAME = "rentwise-property-service";
    
    @Autowired
    private PropertyService propertyService;
    
    // Thymeleaf Web Endpoints
    @GetMapping("/")
    public String index() {
        logger.info("[{}] [PropertyController] [index] Redirecting to /properties", SERVICE_NAME);
        return "redirect:/properties";
    }
    
    @GetMapping("/properties")
    public String listProperties(Model model) {
        logger.info("[{}] [PropertyController] [listProperties] START - API: GET /properties", SERVICE_NAME);
        try {
            List<Property> properties = propertyService.getAllProperties();
            model.addAttribute("properties", properties);
            logger.info("[{}] [PropertyController] [listProperties] SUCCESS - Retrieved {} properties", SERVICE_NAME, properties.size());
            return "properties/list";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [listProperties] ERROR - Failed to retrieve properties: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/properties/new")
    public String showCreateForm(Model model) {
        logger.info("[{}] [PropertyController] [showCreateForm] START - API: GET /properties/new", SERVICE_NAME);
        model.addAttribute("property", new Property());
        logger.debug("[{}] [PropertyController] [showCreateForm] Property creation form displayed", SERVICE_NAME);
        return "properties/form";
    }
    
    @PostMapping("/properties")
    public String createProperty(@ModelAttribute Property property, Model model) {
        logger.info("[{}] [PropertyController] [createProperty] START - API: POST /properties - Property: {}", 
                SERVICE_NAME, property.getName());
        try {
            Property created = propertyService.createProperty(property);
            logger.info("[{}] [PropertyController] [createProperty] SUCCESS - Property created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return "redirect:/properties?success";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [createProperty] ERROR - Failed to create property: {} - Error: {}", 
                    SERVICE_NAME, property.getName(), e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("property", property);
            return "properties/form";
        }
    }
    
    @GetMapping("/properties/{id}")
    public String getPropertyDetails(@PathVariable Long id, Model model) {
        logger.info("[{}] [PropertyController] [getPropertyDetails] START - API: GET /properties/{}", SERVICE_NAME, id);
        try {
            Property property = propertyService.getPropertyById(id);
            if (property != null) {
                model.addAttribute("property", property);
                logger.info("[{}] [PropertyController] [getPropertyDetails] SUCCESS - Property details retrieved for ID: {}", 
                        SERVICE_NAME, id);
                return "properties/details";
            }
            logger.warn("[{}] [PropertyController] [getPropertyDetails] Property not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/properties?error";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [getPropertyDetails] ERROR - Failed to get property details for ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/properties?error";
        }
    }
    
    @GetMapping("/properties/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("[{}] [PropertyController] [showEditForm] START - API: GET /properties/{}/edit", SERVICE_NAME, id);
        try {
            Property property = propertyService.getPropertyById(id);
            if (property != null) {
                model.addAttribute("property", property);
                logger.info("[{}] [PropertyController] [showEditForm] SUCCESS - Edit form displayed for property ID: {}", 
                        SERVICE_NAME, id);
                return "properties/form";
            }
            logger.warn("[{}] [PropertyController] [showEditForm] Property not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/properties?error";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [showEditForm] ERROR - Failed to show edit form for ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/properties?error";
        }
    }
    
    @PostMapping("/properties/{id}")
    public String updateProperty(@PathVariable Long id, @ModelAttribute Property property, Model model) {
        logger.info("[{}] [PropertyController] [updateProperty] START - API: POST /properties/{}", SERVICE_NAME, id);
        try {
            Property updated = propertyService.updateProperty(id, property);
            if (updated != null) {
                logger.info("[{}] [PropertyController] [updateProperty] SUCCESS - Property updated with ID: {}", SERVICE_NAME, id);
                return "redirect:/properties?success";
            }
            logger.warn("[{}] [PropertyController] [updateProperty] Property not found with ID: {}", SERVICE_NAME, id);
            return "redirect:/properties?error";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [updateProperty] ERROR - Failed to update property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("property", property);
            return "properties/form";
        }
    }
    
    @PostMapping("/properties/{id}/delete")
    public String deleteProperty(@PathVariable Long id) {
        logger.info("[{}] [PropertyController] [deleteProperty] START - API: POST /properties/{}/delete", SERVICE_NAME, id);
        try {
            propertyService.deleteProperty(id);
            logger.info("[{}] [PropertyController] [deleteProperty] SUCCESS - Property deleted with ID: {}", SERVICE_NAME, id);
            return "redirect:/properties?success";
        } catch (Exception e) {
            logger.error("[{}] [PropertyController] [deleteProperty] ERROR - Failed to delete property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return "redirect:/properties?error";
        }
    }
}

