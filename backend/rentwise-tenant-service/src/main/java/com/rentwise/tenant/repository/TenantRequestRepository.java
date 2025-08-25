package com.rentwise.tenant.repository;

import com.rentwise.tenant.model.TenantRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRequestRepository extends JpaRepository<TenantRequest, Long> {
    List<TenantRequest> findByRequestedByUserId(Long userId);
    List<TenantRequest> findByStatus(String status);
    List<TenantRequest> findByRequestedByUserIdAndStatus(Long userId, String status);
}

