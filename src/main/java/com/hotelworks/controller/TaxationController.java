package com.hotelworks.controller;

import com.hotelworks.dto.request.TaxationRequest;
import com.hotelworks.dto.response.TaxationResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.TaxationService;
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
@RequestMapping("/api/taxes")
@Tag(name = "Tax Master", description = "Tax Management APIs")
public class TaxationController {
    
    @Autowired
    private TaxationService taxationService;
    
    @PostMapping
    @Operation(summary = "Create tax", description = "Create a new tax")
    public ResponseEntity<ApiResponse<TaxationResponse>> createTax(
            @Valid @RequestBody TaxationRequest request) {
        try {
            TaxationResponse response = taxationService.createTax(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tax created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create tax: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all taxes", description = "Get all available taxes")
    public ResponseEntity<ApiResponse<List<TaxationResponse>>> getAllTaxes() {
        try {
            List<TaxationResponse> responses = taxationService.getAllTaxes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get taxes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{taxId}")
    @Operation(summary = "Get tax by ID", description = "Get tax details by tax ID")
    public ResponseEntity<ApiResponse<TaxationResponse>> getTax(
            @Parameter(description = "Tax ID") @PathVariable String taxId) {
        try {
            TaxationResponse response = taxationService.getTax(taxId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Tax not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{taxId}")
    @Operation(summary = "Update tax", description = "Update tax details")
    public ResponseEntity<ApiResponse<TaxationResponse>> updateTax(
            @Parameter(description = "Tax ID") @PathVariable String taxId,
            @Valid @RequestBody TaxationRequest request) {
        try {
            TaxationResponse response = taxationService.updateTax(taxId, request);
            return ResponseEntity.ok(ApiResponse.success("Tax updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update tax: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{taxId}")
    @Operation(summary = "Delete tax", description = "Delete tax by ID")
    public ResponseEntity<ApiResponse<String>> deleteTax(
            @Parameter(description = "Tax ID") @PathVariable String taxId) {
        try {
            taxationService.deleteTax(taxId);
            return ResponseEntity.ok(ApiResponse.success("Tax deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete tax: " + e.getMessage()));
        }
    }
}