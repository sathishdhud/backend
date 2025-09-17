package com.hotelworks.controller;

import com.hotelworks.dto.request.PaymentModeRequest;
import com.hotelworks.dto.response.PaymentModeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.PaymentModeService;
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
@RequestMapping("/api/settlement-types")
@Tag(name = "Settlement Types", description = "Settlement Type Management APIs")
public class SettlementTypeController {
    
    @Autowired
    private PaymentModeService paymentModeService;
    
    @PostMapping
    @Operation(summary = "Create settlement type", description = "Create a new settlement type")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> createSettlementType(
            @Valid @RequestBody PaymentModeRequest request) {
        try {
            PaymentModeResponse response = paymentModeService.createPaymentMode(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Settlement type created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create settlement type: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all settlement types", description = "Get all available settlement types")
    public ResponseEntity<ApiResponse<List<PaymentModeResponse>>> getAllSettlementTypes() {
        try {
            List<PaymentModeResponse> responses = paymentModeService.getAllPaymentModes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get settlement types: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get settlement type by ID", description = "Get settlement type details by ID")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> getSettlementType(
            @Parameter(description = "Settlement type ID") @PathVariable String id) {
        try {
            PaymentModeResponse response = paymentModeService.getPaymentMode(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Settlement type not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update settlement type", description = "Update settlement type details")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> updateSettlementType(
            @Parameter(description = "Settlement type ID") @PathVariable String id,
            @Valid @RequestBody PaymentModeRequest request) {
        try {
            PaymentModeResponse response = paymentModeService.updatePaymentMode(id, request);
            return ResponseEntity.ok(ApiResponse.success("Settlement type updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update settlement type: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete settlement type", description = "Delete settlement type by ID")
    public ResponseEntity<ApiResponse<String>> deleteSettlementType(
            @Parameter(description = "Settlement type ID") @PathVariable String id) {
        try {
            paymentModeService.deletePaymentMode(id);
            return ResponseEntity.ok(ApiResponse.success("Settlement type deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete settlement type: " + e.getMessage()));
        }
    }
}