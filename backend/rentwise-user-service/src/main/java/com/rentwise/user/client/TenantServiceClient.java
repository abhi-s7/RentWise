package com.rentwise.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "rentwise-tenant-service")
public interface TenantServiceClient {
    
    @PostMapping("/api/tenants")
    Map<String, Object> createTenant(@RequestBody Map<String, Object> tenant);
}

