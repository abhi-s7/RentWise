package com.rentwise.property.controller;

import com.rentwise.property.model.Property;
import com.rentwise.property.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyRestController.class);
    private static final String SERVICE_NAME = "rentwise-property-service";
    
    @Autowired
    private PropertyService propertyService;
    
    @GetMapping
    public List<Property> getAllProperties() {
        logger.info("[{}] [PropertyRestController] [getAllProperties] START - API: GET /api/properties", SERVICE_NAME);
        try {
            List<Property> properties = propertyService.getAllProperties();
            logger.info("[{}] [PropertyRestController] [getAllProperties] SUCCESS - Returning {} properties", SERVICE_NAME, properties.size());
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [getAllProperties] ERROR - Failed to retrieve properties: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        logger.info("[{}] [PropertyRestController] [getPropertyById] START - API: GET /api/properties/{}", SERVICE_NAME, id);
        try {
            Property property = propertyService.getPropertyById(id);
            if (property != null) {
                logger.info("[{}] [PropertyRestController] [getPropertyById] SUCCESS - Property found with ID: {}", SERVICE_NAME, id);
                return ResponseEntity.ok(property);
            }
            logger.warn("[{}] [PropertyRestController] [getPropertyById] Property not found with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [getPropertyById] ERROR - Failed to get property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        logger.info("[{}] [PropertyRestController] [createProperty] START - API: POST /api/properties - Property: {}", 
                SERVICE_NAME, property.getName());
        try {
            Property created = propertyService.createProperty(property);
            logger.info("[{}] [PropertyRestController] [createProperty] SUCCESS - Property created with ID: {}", 
                    SERVICE_NAME, created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [createProperty] ERROR - Failed to create property: {} - Error: {}", 
                    SERVICE_NAME, property.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property property) {
        logger.info("[{}] [PropertyRestController] [updateProperty] START - API: PUT /api/properties/{}", SERVICE_NAME, id);
        try {
            Property updated = propertyService.updateProperty(id, property);
            if (updated != null) {
                logger.info("[{}] [PropertyRestController] [updateProperty] SUCCESS - Property updated with ID: {}", SERVICE_NAME, id);
                return ResponseEntity.ok(updated);
            }
            logger.warn("[{}] [PropertyRestController] [updateProperty] Property not found with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [updateProperty] ERROR - Failed to update property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        logger.info("[{}] [PropertyRestController] [deleteProperty] START - API: DELETE /api/properties/{}", SERVICE_NAME, id);
        try {
            propertyService.deleteProperty(id);
            logger.info("[{}] [PropertyRestController] [deleteProperty] SUCCESS - Property deleted with ID: {}", SERVICE_NAME, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [deleteProperty] ERROR - Failed to delete property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Property>> getPropertiesByUserId(@PathVariable Long userId) {
        logger.info("[{}] [PropertyRestController] [getPropertiesByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<Property> properties = propertyService.getPropertiesByUserId(userId);
            logger.info("[{}] [PropertyRestController] [getPropertiesByUserId] SUCCESS - Found {} properties", 
                    SERVICE_NAME, properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            logger.error("[{}] [PropertyRestController] [getPropertiesByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

