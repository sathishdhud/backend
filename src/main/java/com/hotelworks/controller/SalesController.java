package com.hotelworks.controller;

import com.hotelworks.dto.request.SalesRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.SalesResponse;
import com.hotelworks.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "Hotel Sales Management APIs")
public class SalesController {
    
    @Autowired
    private SalesService salesService;
    
    @PostMapping
    @Operation(summary = "Create sales record", description = "Create a new hotel sales record")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<SalesResponse>> createSales(
            @Valid @RequestBody SalesRequest request) {
        try {
            SalesResponse response = salesService.createSales(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sales record created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create sales record: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{salesId}")
    @Operation(summary = "Get sales by ID", description = "Retrieve a sales record by its ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<SalesResponse>> getSalesById(
            @Parameter(description = "Sales ID") @PathVariable String salesId) {
        try {
            SalesResponse response = salesService.getSalesById(salesId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Sales record not found: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all sales records", description = "Retrieve all sales records")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SalesResponse>>> getAllSales() {
        try {
            List<SalesResponse> responses = salesService.getAllSales();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve sales records: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{salesId}")
    @Operation(summary = "Update sales record", description = "Update an existing sales record")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<SalesResponse>> updateSales(
            @Parameter(description = "Sales ID") @PathVariable String salesId,
            @Valid @RequestBody SalesRequest request) {
        try {
            SalesResponse response = salesService.updateSales(salesId, request);
            return ResponseEntity.ok(ApiResponse.success("Sales record updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update sales record: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{salesId}")
    @Operation(summary = "Delete sales record", description = "Delete a sales record by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSales(
            @Parameter(description = "Sales ID") @PathVariable String salesId) {
        try {
            salesService.deleteSales(salesId);
            return ResponseEntity.ok(ApiResponse.success("Sales record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Failed to delete sales record: " + e.getMessage()));
        }
    }
}