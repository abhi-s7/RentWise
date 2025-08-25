package com.rentwise.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RentwiseApiGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(RentwiseApiGatewayApplication.class);
	private static final String SERVICE_NAME = "rentwise-api-gateway";

	public static void main(String[] args) {
		logger.info("[{}] [RentwiseApiGatewayApplication] [main] START - Starting API Gateway application", SERVICE_NAME);
		try {
		SpringApplication.run(RentwiseApiGatewayApplication.class, args);
			logger.info("[{}] [RentwiseApiGatewayApplication] [main] SUCCESS - API Gateway started successfully", SERVICE_NAME);
		} catch (Exception e) {
			logger.error("[{}] [RentwiseApiGatewayApplication] [main] ERROR - Failed to start API Gateway: {}", 
					SERVICE_NAME, e.getMessage(), e);
			throw e;
		}
	}

}
