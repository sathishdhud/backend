package com.hotelworks.controller;

import com.hotelworks.dto.request.RefModeRequest;
import com.hotelworks.dto.response.RefModeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.RefModeService;
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
@RequestMapping("/api/ref-modes")
@Tag(name = "Reference Modes", description = "Reference Mode Management APIs")
public class RefModeController {
    
    @Autowired
    private RefModeService refModeService;
    
    @PostMapping
    @Operation(summary = "Create reference mode", description = "Create a new reference mode")
    public ResponseEntity<ApiResponse<RefModeResponse>> createRefMode(
            @Valid @RequestBody RefModeRequest request) {
        try {
            RefModeResponse response = refModeService.createRefMode(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reference mode created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create reference mode: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all reference modes", description = "Get all available reference modes")
    public ResponseEntity<ApiResponse<List<RefModeResponse>>> getAllRefModes() {
        try {
            List<RefModeResponse> responses = refModeService.getAllRefModes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get reference modes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get reference mode by ID", description = "Get reference mode details by ID")
    public ResponseEntity<ApiResponse<RefModeResponse>> getRefMode(
            @Parameter(description = "Reference Mode ID") @PathVariable String id) {
        try {
            RefModeResponse response = refModeService.getRefMode(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Reference mode not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update reference mode", description = "Update reference mode details")
    public ResponseEntity<ApiResponse<RefModeResponse>> updateRefMode(
            @Parameter(description = "Reference Mode ID") @PathVariable String id,
            @Valid @RequestBody RefModeRequest request) {
        try {
            RefModeResponse response = refModeService.updateRefMode(id, request);
            return ResponseEntity.ok(ApiResponse.success("Reference mode updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update reference mode: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reference mode", description = "Delete reference mode by ID")
    public ResponseEntity<ApiResponse<String>> deleteRefMode(
            @Parameter(description = "Reference Mode ID") @PathVariable String id) {
        try {
            refModeService.deleteRefMode(id);
            return ResponseEntity.ok(ApiResponse.success("Reference mode deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete reference mode: " + e.getMessage()));
        }
    }
}