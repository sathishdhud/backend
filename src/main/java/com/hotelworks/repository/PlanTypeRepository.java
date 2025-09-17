package com.hotelworks.repository;

import com.hotelworks.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanTypeRepository extends JpaRepository<PlanType, String> {
    
    Optional<PlanType> findByPlanName(String planName);
    
    boolean existsByPlanName(String planName);
}