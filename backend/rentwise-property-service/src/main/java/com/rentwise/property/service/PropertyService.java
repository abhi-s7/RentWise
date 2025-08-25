package com.rentwise.property.service;

import com.rentwise.property.model.Property;
import com.rentwise.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    private static final String SERVICE_NAME = "rentwise-property-service";
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    public List<Property> getAllProperties() {
        logger.info("[{}] [PropertyService] [getAllProperties] START - Fetching all properties", SERVICE_NAME);
        try {
            List<Property> properties = propertyRepository.findAll();
            logger.info("[{}] [PropertyService] [getAllProperties] SUCCESS - Found {} properties", SERVICE_NAME, properties.size());
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [getAllProperties] ERROR - Failed to fetch properties: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public Property getPropertyById(Long id) {
        logger.info("[{}] [PropertyService] [getPropertyById] START - Fetching property with ID: {}", SERVICE_NAME, id);
        try {
            Property property = propertyRepository.findById(id).orElse(null);
            if (property != null) {
                logger.info("[{}] [PropertyService] [getPropertyById] SUCCESS - Property found with ID: {}", SERVICE_NAME, id);
            } else {
                logger.warn("[{}] [PropertyService] [getPropertyById] Property not found with ID: {}", SERVICE_NAME, id);
            }
            return property;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [getPropertyById] ERROR - Failed to fetch property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public Property createProperty(Property property) {
        logger.info("[{}] [PropertyService] [createProperty] START - Creating property: {}", SERVICE_NAME, property.getName());
        try {
            if (property.getStatus() == null || property.getStatus().isEmpty()) {
                logger.debug("[{}] [PropertyService] [createProperty] Setting default status to AVAILABLE", SERVICE_NAME);
                property.setStatus("AVAILABLE");
            }
            Property savedProperty = propertyRepository.save(property);
            logger.info("[{}] [PropertyService] [createProperty] SUCCESS - Property created with ID: {}", SERVICE_NAME, savedProperty.getId());
            return savedProperty;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [createProperty] ERROR - Failed to create property: {} - Error: {}", 
                    SERVICE_NAME, property.getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    public Property updateProperty(Long id, Property property) {
        logger.info("[{}] [PropertyService] [updateProperty] START - Updating property with ID: {}", SERVICE_NAME, id);
        try {
            Property existing = propertyRepository.findById(id).orElse(null);
            if (existing != null) {
                property.setId(id);
                Property updated = propertyRepository.save(property);
                logger.info("[{}] [PropertyService] [updateProperty] SUCCESS - Property updated with ID: {}", SERVICE_NAME, id);
                return updated;
            }
            logger.warn("[{}] [PropertyService] [updateProperty] Property not found with ID: {}", SERVICE_NAME, id);
            return null;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [updateProperty] ERROR - Failed to update property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public void deleteProperty(Long id) {
        logger.info("[{}] [PropertyService] [deleteProperty] START - Deleting property with ID: {}", SERVICE_NAME, id);
        try {
            propertyRepository.deleteById(id);
            logger.info("[{}] [PropertyService] [deleteProperty] SUCCESS - Property deleted with ID: {}", SERVICE_NAME, id);
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [deleteProperty] ERROR - Failed to delete property with ID: {} - Error: {}", 
                    SERVICE_NAME, id, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Property> getPropertiesByStatus(String status) {
        logger.info("[{}] [PropertyService] [getPropertiesByStatus] START - Fetching properties with status: {}", SERVICE_NAME, status);
        try {
            List<Property> properties = propertyRepository.findByStatus(status);
            logger.info("[{}] [PropertyService] [getPropertiesByStatus] SUCCESS - Found {} properties with status: {}", 
                    SERVICE_NAME, properties.size(), status);
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [getPropertiesByStatus] ERROR - Failed to fetch properties with status: {} - Error: {}", 
                    SERVICE_NAME, status, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Property> getPropertiesByCity(String city) {
        logger.info("[{}] [PropertyService] [getPropertiesByCity] START - Fetching properties in city: {}", SERVICE_NAME, city);
        try {
            List<Property> properties = propertyRepository.findByCity(city);
            logger.info("[{}] [PropertyService] [getPropertiesByCity] SUCCESS - Found {} properties in city: {}", 
                    SERVICE_NAME, properties.size(), city);
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [getPropertiesByCity] ERROR - Failed to fetch properties in city: {} - Error: {}", 
                    SERVICE_NAME, city, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Property> getPropertiesByUserId(Long userId) {
        logger.info("[{}] [PropertyService] [getPropertiesByUserId] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<Property> properties = propertyRepository.findByUserId(userId);
            logger.info("[{}] [PropertyService] [getPropertiesByUserId] SUCCESS - Found {} properties for user {}", 
                    SERVICE_NAME, properties.size(), userId);
            return properties;
        } catch (Exception e) {
            logger.error("[{}] [PropertyService] [getPropertiesByUserId] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
}

