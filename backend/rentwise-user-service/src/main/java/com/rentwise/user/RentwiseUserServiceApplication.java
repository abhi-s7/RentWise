package com.rentwise.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RentwiseUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentwiseUserServiceApplication.class, args);
	}

}
