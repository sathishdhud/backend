package com.hotelworks.controller;

import com.hotelworks.dto.request.ArrivalModeRequest;
import com.hotelworks.dto.response.ArrivalModeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.ArrivalModeService;
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
@RequestMapping("/api/arrival-modes")
@Tag(name = "Arrival Modes", description = "Arrival Mode Management APIs")
public class ArrivalModeController {
    
    @Autowired
    private ArrivalModeService arrivalModeService;
    
    @PostMapping
    @Operation(summary = "Create arrival mode", description = "Create a new arrival mode")
    public ResponseEntity<ApiResponse<ArrivalModeResponse>> createArrivalMode(
            @Valid @RequestBody ArrivalModeRequest request) {
        try {
            ArrivalModeResponse response = arrivalModeService.createArrivalMode(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Arrival mode created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create arrival mode: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all arrival modes", description = "Get all available arrival modes")
    public ResponseEntity<ApiResponse<List<ArrivalModeResponse>>> getAllArrivalModes() {
        try {
            List<ArrivalModeResponse> responses = arrivalModeService.getAllArrivalModes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get arrival modes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get arrival mode by ID", description = "Get arrival mode details by ID")
    public ResponseEntity<ApiResponse<ArrivalModeResponse>> getArrivalMode(
            @Parameter(description = "Arrival Mode ID") @PathVariable String id) {
        try {
            ArrivalModeResponse response = arrivalModeService.getArrivalMode(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Arrival mode not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update arrival mode", description = "Update arrival mode details")
    public ResponseEntity<ApiResponse<ArrivalModeResponse>> updateArrivalMode(
            @Parameter(description = "Arrival Mode ID") @PathVariable String id,
            @Valid @RequestBody ArrivalModeRequest request) {
        try {
            ArrivalModeResponse response = arrivalModeService.updateArrivalMode(id, request);
            return ResponseEntity.ok(ApiResponse.success("Arrival mode updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update arrival mode: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete arrival mode", description = "Delete arrival mode by ID")
    public ResponseEntity<ApiResponse<String>> deleteArrivalMode(
            @Parameter(description = "Arrival Mode ID") @PathVariable String id) {
        try {
            arrivalModeService.deleteArrivalMode(id);
            return ResponseEntity.ok(ApiResponse.success("Arrival mode deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete arrival mode: " + e.getMessage()));
        }
    }
}