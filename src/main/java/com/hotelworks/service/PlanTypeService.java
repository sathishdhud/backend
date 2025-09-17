package com.hotelworks.service;

import com.hotelworks.dto.request.PlanTypeRequest;
import com.hotelworks.dto.response.PlanTypeResponse;
import com.hotelworks.entity.PlanType;
import com.hotelworks.repository.PlanTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanTypeService {
    
    @Autowired
    private PlanTypeRepository planTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new plan type
     */
    public PlanTypeResponse createPlanType(PlanTypeRequest request) {
        // Generate plan ID if not provided
        String planId = request.getPlanId();
        if (planId == null || planId.trim().isEmpty()) {
            planId = numberGenerationService.generatePlanId();
        }
        
        // Check if plan type already exists
        if (planTypeRepository.existsById(planId)) {
            throw new RuntimeException("Plan type already exists: " + planId);
        }
        
        PlanType planType = new PlanType();
        planType.setPlanId(planId);
        planType.setPlanName(request.getPlanName());
        planType.setDiscountPercentage(request.getDiscountPercentage());
        
        PlanType savedPlanType = planTypeRepository.save(planType);
        return mapToPlanTypeResponse(savedPlanType);
    }
    
    /**
     * Get all plan types
     */
    public List<PlanTypeResponse> getAllPlanTypes() {
        List<PlanType> planTypes = planTypeRepository.findAll();
        return planTypes.stream()
            .map(this::mapToPlanTypeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get plan type by ID
     */
    public PlanTypeResponse getPlanType(String planId) {
        PlanType planType = planTypeRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan type not found: " + planId));
        return mapToPlanTypeResponse(planType);
    }
    
    /**
     * Update plan type
     */
    public PlanTypeResponse updatePlanType(String planId, PlanTypeRequest request) {
        PlanType planType = planTypeRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan type not found: " + planId));
        
        planType.setPlanName(request.getPlanName());
        planType.setDiscountPercentage(request.getDiscountPercentage());
        
        PlanType updatedPlanType = planTypeRepository.save(planType);
        return mapToPlanTypeResponse(updatedPlanType);
    }
    
    /**
     * Delete plan type
     */
    public void deletePlanType(String planId) {
        if (!planTypeRepository.existsById(planId)) {
            throw new RuntimeException("Plan type not found: " + planId);
        }
        planTypeRepository.deleteById(planId);
    }
    
    private PlanTypeResponse mapToPlanTypeResponse(PlanType planType) {
        PlanTypeResponse response = new PlanTypeResponse();
        response.setPlanId(planType.getPlanId());
        response.setPlanName(planType.getPlanName());
        response.setDiscountPercentage(planType.getDiscountPercentage());
        return response;
    }
}