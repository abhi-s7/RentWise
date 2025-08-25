package com.rentwise.tenant.service;

import com.rentwise.tenant.config.RabbitMQConfig;
import com.rentwise.tenant.model.Tenant;
import com.rentwise.tenant.model.TenantRequest;
import com.rentwise.tenant.model.TenantRequestEvent;
import com.rentwise.tenant.repository.TenantRepository;
import com.rentwise.tenant.repository.TenantRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantRequestService {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantRequestService.class);
    private static final String SERVICE_NAME = "rentwise-tenant-service";
    
    @Autowired
    private TenantRequestRepository tenantRequestRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public TenantRequest createTenantRequest(TenantRequest request) throws Exception {
        logger.info("[{}] [TenantRequestService] [createTenantRequest] START - Email: {}", SERVICE_NAME, request.getEmail());
        try {
            // Check if email already exists in tenants
            if (tenantRepository.existsByEmail(request.getEmail())) {
                logger.warn("[{}] [TenantRequestService] [createTenantRequest] Email already exists as tenant: {}", 
                        SERVICE_NAME, request.getEmail());
                throw new Exception("Email already exists as a tenant");
            }
            
            // Check if there's already a pending request with this email
            List<TenantRequest> existingRequests = tenantRequestRepository.findByStatus("PENDING");
            boolean emailExists = existingRequests.stream()
                    .anyMatch(r -> r.getEmail().equalsIgnoreCase(request.getEmail()));
            if (emailExists) {
                logger.warn("[{}] [TenantRequestService] [createTenantRequest] Pending request already exists for email: {}", 
                        SERVICE_NAME, request.getEmail());
                throw new Exception("A pending request already exists for this email");
            }
            
            request.setStatus("PENDING");
            TenantRequest saved = tenantRequestRepository.save(request);
            
            // Publish event to RabbitMQ
            try {
                TenantRequestEvent event = new TenantRequestEvent(
                    saved.getId(),
                    saved.getRequestedByUserId(),
                    "CREATED",
                    saved.getEmail(),
                    saved.getFirstName(),
                    saved.getLastName()
                );
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_TENANT_REQUEST,
                    RabbitMQConfig.ROUTING_KEY_TENANT_REQUEST,
                    event
                );
                logger.info("[{}] [TenantRequestService] [createTenantRequest] Event published to RabbitMQ", SERVICE_NAME);
            } catch (Exception e) {
                logger.warn("[{}] [TenantRequestService] [createTenantRequest] Failed to publish event: {}", 
                        SERVICE_NAME, e.getMessage());
                // Don't fail the request if RabbitMQ fails
            }
            
            logger.info("[{}] [TenantRequestService] [createTenantRequest] SUCCESS - Request created with ID: {}", 
                    SERVICE_NAME, saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [createTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantRequest> getAllTenantRequests() {
        logger.info("[{}] [TenantRequestService] [getAllTenantRequests] START", SERVICE_NAME);
        try {
            List<TenantRequest> requests = tenantRequestRepository.findAll();
            logger.info("[{}] [TenantRequestService] [getAllTenantRequests] SUCCESS - Found {} requests", 
                    SERVICE_NAME, requests.size());
            return requests;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [getAllTenantRequests] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantRequest> getTenantRequestsByUser(Long userId) {
        logger.info("[{}] [TenantRequestService] [getTenantRequestsByUser] START - User ID: {}", SERVICE_NAME, userId);
        try {
            List<TenantRequest> requests = tenantRequestRepository.findByRequestedByUserId(userId);
            logger.info("[{}] [TenantRequestService] [getTenantRequestsByUser] SUCCESS - Found {} requests for user {}", 
                    SERVICE_NAME, requests.size(), userId);
            return requests;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [getTenantRequestsByUser] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<TenantRequest> getPendingTenantRequests() {
        logger.info("[{}] [TenantRequestService] [getPendingTenantRequests] START", SERVICE_NAME);
        try {
            List<TenantRequest> requests = tenantRequestRepository.findByStatus("PENDING");
            logger.info("[{}] [TenantRequestService] [getPendingTenantRequests] SUCCESS - Found {} pending requests", 
                    SERVICE_NAME, requests.size());
            return requests;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [getPendingTenantRequests] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantRequest approveTenantRequest(Long requestId) throws Exception {
        logger.info("[{}] [TenantRequestService] [approveTenantRequest] START - Request ID: {}", SERVICE_NAME, requestId);
        try {
            TenantRequest request = tenantRequestRepository.findById(requestId)
                    .orElseThrow(() -> new Exception("Tenant request not found"));
            
            if (!"PENDING".equals(request.getStatus())) {
                throw new Exception("Only pending requests can be approved");
            }
            
            // Create tenant from request
            Tenant tenant = new Tenant();
            tenant.setFirstName(request.getFirstName());
            tenant.setLastName(request.getLastName());
            tenant.setEmail(request.getEmail());
            tenant.setPhone(request.getPhone());
            tenant.setUserId(request.getRequestedByUserId());
            
            // Check if email already exists
            if (tenantRepository.existsByEmail(tenant.getEmail())) {
                throw new Exception("Email already exists as a tenant");
            }
            
            Tenant savedTenant = tenantRepository.save(tenant);
            logger.info("[{}] [TenantRequestService] [approveTenantRequest] Tenant created with ID: {}", 
                    SERVICE_NAME, savedTenant.getId());
            
            // Update request status
            request.setStatus("APPROVED");
            TenantRequest updated = tenantRequestRepository.save(request);
            
            // Publish event to RabbitMQ
            try {
                TenantRequestEvent event = new TenantRequestEvent(
                    updated.getId(),
                    updated.getRequestedByUserId(),
                    "APPROVED",
                    updated.getEmail(),
                    updated.getFirstName(),
                    updated.getLastName()
                );
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_TENANT_REQUEST,
                    RabbitMQConfig.ROUTING_KEY_TENANT_REQUEST,
                    event
                );
                logger.info("[{}] [TenantRequestService] [approveTenantRequest] Event published to RabbitMQ", SERVICE_NAME);
            } catch (Exception e) {
                logger.warn("[{}] [TenantRequestService] [approveTenantRequest] Failed to publish event: {}", 
                        SERVICE_NAME, e.getMessage());
                // Don't fail the approval if RabbitMQ fails
            }
            
            logger.info("[{}] [TenantRequestService] [approveTenantRequest] SUCCESS - Request approved", SERVICE_NAME);
            return updated;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [approveTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    public TenantRequest rejectTenantRequest(Long requestId) throws Exception {
        logger.info("[{}] [TenantRequestService] [rejectTenantRequest] START - Request ID: {}", SERVICE_NAME, requestId);
        try {
            TenantRequest request = tenantRequestRepository.findById(requestId)
                    .orElseThrow(() -> new Exception("Tenant request not found"));
            
            if (!"PENDING".equals(request.getStatus())) {
                throw new Exception("Only pending requests can be rejected");
            }
            
            request.setStatus("REJECTED");
            TenantRequest updated = tenantRequestRepository.save(request);
            
            // Publish event to RabbitMQ
            try {
                TenantRequestEvent event = new TenantRequestEvent(
                    updated.getId(),
                    updated.getRequestedByUserId(),
                    "REJECTED",
                    updated.getEmail(),
                    updated.getFirstName(),
                    updated.getLastName()
                );
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_TENANT_REQUEST,
                    RabbitMQConfig.ROUTING_KEY_TENANT_REQUEST,
                    event
                );
                logger.info("[{}] [TenantRequestService] [rejectTenantRequest] Event published to RabbitMQ", SERVICE_NAME);
            } catch (Exception e) {
                logger.warn("[{}] [TenantRequestService] [rejectTenantRequest] Failed to publish event: {}", 
                        SERVICE_NAME, e.getMessage());
                // Don't fail the rejection if RabbitMQ fails
            }
            
            logger.info("[{}] [TenantRequestService] [rejectTenantRequest] SUCCESS - Request rejected", SERVICE_NAME);
            return updated;
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestService] [rejectTenantRequest] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
}

