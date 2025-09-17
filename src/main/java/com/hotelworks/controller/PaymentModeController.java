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
@RequestMapping("/api/payment-modes")
@Tag(name = "Payment Modes", description = "Payment Mode Management APIs")
public class PaymentModeController {
    
    @Autowired
    private PaymentModeService paymentModeService;
    
    @PostMapping
    @Operation(summary = "Create payment mode", description = "Create a new payment mode")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> createPaymentMode(
            @Valid @RequestBody PaymentModeRequest request) {
        try {
            PaymentModeResponse response = paymentModeService.createPaymentMode(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment mode created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create payment mode: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all payment modes", description = "Get all available payment modes")
    public ResponseEntity<ApiResponse<List<PaymentModeResponse>>> getAllPaymentModes() {
        try {
            List<PaymentModeResponse> responses = paymentModeService.getAllPaymentModes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get payment modes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get payment mode by ID", description = "Get payment mode details by ID")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> getPaymentMode(
            @Parameter(description = "Payment mode ID") @PathVariable String id) {
        try {
            PaymentModeResponse response = paymentModeService.getPaymentMode(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Payment mode not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update payment mode", description = "Update payment mode details")
    public ResponseEntity<ApiResponse<PaymentModeResponse>> updatePaymentMode(
            @Parameter(description = "Payment mode ID") @PathVariable String id,
            @Valid @RequestBody PaymentModeRequest request) {
        try {
            PaymentModeResponse response = paymentModeService.updatePaymentMode(id, request);
            return ResponseEntity.ok(ApiResponse.success("Payment mode updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update payment mode: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment mode", description = "Delete payment mode by ID")
    public ResponseEntity<ApiResponse<String>> deletePaymentMode(
            @Parameter(description = "Payment mode ID") @PathVariable String id) {
        try {
            paymentModeService.deletePaymentMode(id);
            return ResponseEntity.ok(ApiResponse.success("Payment mode deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete payment mode: " + e.getMessage()));
        }
    }
}