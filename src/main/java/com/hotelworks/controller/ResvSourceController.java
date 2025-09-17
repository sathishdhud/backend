package com.hotelworks.controller;

import com.hotelworks.dto.request.ResvSourceRequest;
import com.hotelworks.dto.response.ResvSourceResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.ResvSourceService;
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
@RequestMapping("/api/reservation-sources")
@Tag(name = "Reservation Sources", description = "Reservation Source Management APIs")
public class ResvSourceController {
    
    @Autowired
    private ResvSourceService resvSourceService;
    
    @PostMapping
    @Operation(summary = "Create reservation source", description = "Create a new reservation source")
    public ResponseEntity<ApiResponse<ResvSourceResponse>> createResvSource(
            @Valid @RequestBody ResvSourceRequest request) {
        try {
            ResvSourceResponse response = resvSourceService.createResvSource(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation source created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create reservation source: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all reservation sources", description = "Get all available reservation sources")
    public ResponseEntity<ApiResponse<List<ResvSourceResponse>>> getAllResvSources() {
        try {
            List<ResvSourceResponse> responses = resvSourceService.getAllResvSources();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get reservation sources: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get reservation source by ID", description = "Get reservation source details by ID")
    public ResponseEntity<ApiResponse<ResvSourceResponse>> getResvSource(
            @Parameter(description = "Reservation Source ID") @PathVariable String id) {
        try {
            ResvSourceResponse response = resvSourceService.getResvSource(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Reservation source not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update reservation source", description = "Update reservation source details")
    public ResponseEntity<ApiResponse<ResvSourceResponse>> updateResvSource(
            @Parameter(description = "Reservation Source ID") @PathVariable String id,
            @Valid @RequestBody ResvSourceRequest request) {
        try {
            ResvSourceResponse response = resvSourceService.updateResvSource(id, request);
            return ResponseEntity.ok(ApiResponse.success("Reservation source updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update reservation source: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation source", description = "Delete reservation source by ID")
    public ResponseEntity<ApiResponse<String>> deleteResvSource(
            @Parameter(description = "Reservation Source ID") @PathVariable String id) {
        try {
            resvSourceService.deleteResvSource(id);
            return ResponseEntity.ok(ApiResponse.success("Reservation source deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete reservation source: " + e.getMessage()));
        }
    }
}