package com.rentwise.dashboard.consumer;

import com.rentwise.dashboard.model.TenantRequestEvent;
import com.rentwise.dashboard.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TenantRequestConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantRequestConsumer.class);
    private static final String SERVICE_NAME = "rentwise-dashboard-service";
    
    @Autowired
    private NotificationService notificationService;
    
    @RabbitListener(queues = "tenant.request.queue")
    public void handleTenantRequestEvent(TenantRequestEvent event) {
        logger.info("[{}] [TenantRequestConsumer] [handleTenantRequestEvent] Received event - Request ID: {}, Status: {}, User ID: {}", 
                SERVICE_NAME, event.getRequestId(), event.getStatus(), event.getRequestedByUserId());
        
        try {
            if ("CREATED".equals(event.getStatus())) {
                logger.info("[{}] [TenantRequestConsumer] New tenant request created - Request ID: {}, Requested by User: {}, Email: {}", 
                        SERVICE_NAME, event.getRequestId(), event.getRequestedByUserId(), event.getEmail());
                logger.info("[{}] [TenantRequestConsumer] Admin should be notified about new request", SERVICE_NAME);
            } else if ("APPROVED".equals(event.getStatus())) {
                logger.info("[{}] [TenantRequestConsumer] Tenant request approved - Request ID: {}, Requested by User: {}", 
                        SERVICE_NAME, event.getRequestId(), event.getRequestedByUserId());
                logger.info("[{}] [TenantRequestConsumer] User {} should be notified about approval", 
                        SERVICE_NAME, event.getRequestedByUserId());
            } else if ("REJECTED".equals(event.getStatus())) {
                logger.info("[{}] [TenantRequestConsumer] Tenant request rejected - Request ID: {}, Requested by User: {}", 
                        SERVICE_NAME, event.getRequestId(), event.getRequestedByUserId());
                logger.info("[{}] [TenantRequestConsumer] User {} should be notified about rejection", 
                        SERVICE_NAME, event.getRequestedByUserId());
            }
            
            // Send WebSocket notification
            notificationService.sendNotification(event);
            
        } catch (Exception e) {
            logger.error("[{}] [TenantRequestConsumer] [handleTenantRequestEvent] ERROR processing event: {}", 
                    SERVICE_NAME, e.getMessage(), e);
        }
    }
}

