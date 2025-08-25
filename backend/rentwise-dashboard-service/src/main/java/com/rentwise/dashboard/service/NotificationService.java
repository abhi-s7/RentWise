package com.rentwise.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentwise.dashboard.model.TenantRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String SERVICE_NAME = "rentwise-dashboard-service";
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void sendNotification(TenantRequestEvent event) {
        try {
            logger.info("[{}] [NotificationService] [sendNotification] Broadcasting event - Type: {}, Request ID: {}, User ID: {}", 
                    SERVICE_NAME, event.getStatus(), event.getRequestId(), event.getRequestedByUserId());
            
            // Broadcast to all connected clients
            messagingTemplate.convertAndSend("/topic/notifications", event);
            
            logger.info("[{}] [NotificationService] [sendNotification] SUCCESS - Notification sent", SERVICE_NAME);
        } catch (Exception e) {
            logger.error("[{}] [NotificationService] [sendNotification] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
        }
    }
}

