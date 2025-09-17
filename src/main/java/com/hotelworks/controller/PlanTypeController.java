package com.hotelworks.controller;

import com.hotelworks.dto.request.PlanTypeRequest;
import com.hotelworks.dto.response.PlanTypeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.PlanTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan-types")
@Tag(name = "Plan Types", description = "Plan Type Management APIs")
public class PlanTypeController {
    
    @Autowired
    private PlanTypeService planTypeService;
    
    @PostMapping
    @Operation(summary = "Create plan type", description = "Create a new plan type")
    public ResponseEntity<ApiResponse<PlanTypeResponse>> createPlanType(
            @Valid @RequestBody PlanTypeRequest request) {
        try {
            PlanTypeResponse response = planTypeService.createPlanType(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan type created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create plan type: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all plan types", description = "Get all available plan types")
    public ResponseEntity<ApiResponse<List<PlanTypeResponse>>> getAllPlanTypes() {
        try {
            List<PlanTypeResponse> responses = planTypeService.getAllPlanTypes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get plan types: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{planId}")
    @Operation(summary = "Get plan type by ID", description = "Get plan type details by plan ID")
    public ResponseEntity<ApiResponse<PlanTypeResponse>> getPlanType(
            @Parameter(description = "Plan ID") @PathVariable String planId) {
        try {
            PlanTypeResponse response = planTypeService.getPlanType(planId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Plan type not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{planId}")
    @Operation(summary = "Update plan type", description = "Update plan type details")
    public ResponseEntity<ApiResponse<PlanTypeResponse>> updatePlanType(
            @Parameter(description = "Plan ID") @PathVariable String planId,
            @Valid @RequestBody PlanTypeRequest request) {
        try {
            PlanTypeResponse response = planTypeService.updatePlanType(planId, request);
            return ResponseEntity.ok(ApiResponse.success("Plan type updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update plan type: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{planId}")
    @Operation(summary = "Delete plan type", description = "Delete plan type by ID")
    public ResponseEntity<ApiResponse<String>> deletePlanType(
            @Parameter(description = "Plan ID") @PathVariable String planId) {
        try {
            planTypeService.deletePlanType(planId);
            return ResponseEntity.ok(ApiResponse.success("Plan type deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete plan type: " + e.getMessage()));
        }
    }
}