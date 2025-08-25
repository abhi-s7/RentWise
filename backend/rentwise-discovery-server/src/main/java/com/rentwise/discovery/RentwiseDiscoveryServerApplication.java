package com.rentwise.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RentwiseDiscoveryServerApplication {

	private static final Logger logger = LoggerFactory.getLogger(RentwiseDiscoveryServerApplication.class);
	private static final String SERVICE_NAME = "rentwise-discovery-server";

	public static void main(String[] args) {
		logger.info("[{}] [RentwiseDiscoveryServerApplication] [main] START - Starting Eureka Discovery Server", SERVICE_NAME);
		try {
		SpringApplication.run(RentwiseDiscoveryServerApplication.class, args);
			logger.info("[{}] [RentwiseDiscoveryServerApplication] [main] SUCCESS - Eureka Discovery Server started successfully", SERVICE_NAME);
		} catch (Exception e) {
			logger.error("[{}] [RentwiseDiscoveryServerApplication] [main] ERROR - Failed to start Discovery Server: {}", 
					SERVICE_NAME, e.getMessage(), e);
			throw e;
		}
	}

}
