package com.hotelworks.controller;

import com.hotelworks.dto.request.NationalityRequest;
import com.hotelworks.dto.response.NationalityResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.NationalityService;
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
@RequestMapping("/api/nationalities")
@Tag(name = "Nationalities", description = "Nationality Management APIs")
public class NationalityController {
    
    @Autowired
    private NationalityService nationalityService;
    
    @PostMapping
    @Operation(summary = "Create nationality", description = "Create a new nationality")
    public ResponseEntity<ApiResponse<NationalityResponse>> createNationality(
            @Valid @RequestBody NationalityRequest request) {
        try {
            NationalityResponse response = nationalityService.createNationality(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nationality created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create nationality: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all nationalities", description = "Get all available nationalities")
    public ResponseEntity<ApiResponse<List<NationalityResponse>>> getAllNationalities() {
        try {
            List<NationalityResponse> responses = nationalityService.getAllNationalities();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get nationalities: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get nationality by ID", description = "Get nationality details by ID")
    public ResponseEntity<ApiResponse<NationalityResponse>> getNationality(
            @Parameter(description = "Nationality ID") @PathVariable String id) {
        try {
            NationalityResponse response = nationalityService.getNationality(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Nationality not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update nationality", description = "Update nationality details")
    public ResponseEntity<ApiResponse<NationalityResponse>> updateNationality(
            @Parameter(description = "Nationality ID") @PathVariable String id,
            @Valid @RequestBody NationalityRequest request) {
        try {
            NationalityResponse response = nationalityService.updateNationality(id, request);
            return ResponseEntity.ok(ApiResponse.success("Nationality updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update nationality: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete nationality", description = "Delete nationality by ID")
    public ResponseEntity<ApiResponse<String>> deleteNationality(
            @Parameter(description = "Nationality ID") @PathVariable String id) {
        try {
            nationalityService.deleteNationality(id);
            return ResponseEntity.ok(ApiResponse.success("Nationality deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete nationality: " + e.getMessage()));
        }
    }
}