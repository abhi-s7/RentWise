package com.rentwise.property.repository;

import com.rentwise.property.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByStatus(String status);
    List<Property> findByCity(String city);
    List<Property> findByType(String type);
    List<Property> findByUserId(Long userId);
}

