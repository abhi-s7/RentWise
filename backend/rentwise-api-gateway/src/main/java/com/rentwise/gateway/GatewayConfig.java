package com.rentwise.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);
    private static final String SERVICE_NAME = "rentwise-api-gateway";
    
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        logger.info("[{}] [GatewayConfig] [gatewayRoutes] START - Configuring API Gateway routes", SERVICE_NAME);
        try {
            RouteLocator routeLocator = builder.routes()
                    .route(r -> r.path("/api/users/**")
                            .uri("lb://rentwise-user-service"))
                    .route(r -> r.path("/api/properties/**")
                            .uri("lb://rentwise-property-service"))
                    .route(r -> r.path("/api/tenants/**")
                            .uri("lb://rentwise-tenant-service"))
                    .route(r -> r.path("/dashboard/**", "/login/**")
                            .uri("lb://rentwise-dashboard-service"))
                    .build();
            
            logger.info("[{}] [GatewayConfig] [gatewayRoutes] SUCCESS - Routes configured:", SERVICE_NAME);
            logger.info("[{}] [GatewayConfig] [gatewayRoutes] Route: /api/users/** -> lb://rentwise-user-service", SERVICE_NAME);
            logger.info("[{}] [GatewayConfig] [gatewayRoutes] Route: /api/properties/** -> lb://rentwise-property-service", SERVICE_NAME);
            logger.info("[{}] [GatewayConfig] [gatewayRoutes] Route: /api/tenants/** -> lb://rentwise-tenant-service", SERVICE_NAME);
            logger.info("[{}] [GatewayConfig] [gatewayRoutes] Route: /dashboard/** -> lb://rentwise-dashboard-service", SERVICE_NAME);
            
            return routeLocator;
        } catch (Exception e) {
            logger.error("[{}] [GatewayConfig] [gatewayRoutes] ERROR - Failed to configure routes: {}", 
                    SERVICE_NAME, e.getMessage(), e);
            throw e;
        }
    }
}

