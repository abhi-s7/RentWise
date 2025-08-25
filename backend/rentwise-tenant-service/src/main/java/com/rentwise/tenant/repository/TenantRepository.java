package com.rentwise.tenant.repository;

import com.rentwise.tenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Tenant> findByUserId(Long userId);
    List<Tenant> findByPropertyId(Long propertyId);
}

