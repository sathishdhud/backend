package com.hotelworks.controller;

import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.entity.Refund;
import com.hotelworks.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@Tag(name = "Refund Management", description = "APIs for managing refunds")
public class RefundController {
    
    @Autowired
    private RefundService refundService;
    
    @PostMapping
    @Operation(summary = "Create refund", description = "Create a new refund")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Refund>> createRefund(@Valid @RequestBody Refund refund) {
        try {
            Refund createdRefund = refundService.createRefund(refund);
            return ResponseEntity.ok(ApiResponse.success("Refund created successfully", createdRefund));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create refund: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{refundId}")
    @Operation(summary = "Get refund by ID", description = "Retrieve a refund by its ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Refund>> getRefundById(@PathVariable String refundId) {
        try {
            Refund refund = refundService.getRefundById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found with ID: " + refundId));
            return ResponseEntity.ok(ApiResponse.success(refund));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve refund: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all refunds", description = "Retrieve all refunds")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Refund>>> getAllRefunds() {
        try {
            List<Refund> refunds = refundService.getAllRefunds();
            return ResponseEntity.ok(ApiResponse.success(refunds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve refunds: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}")
    @Operation(summary = "Get refunds by bill number", description = "Retrieve refunds by bill number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Refund>>> getRefundsByBillNo(@PathVariable String billNo) {
        try {
            List<Refund> refunds = refundService.getRefundsByBillNo(billNo);
            return ResponseEntity.ok(ApiResponse.success(refunds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve refunds: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}")
    @Operation(summary = "Get refunds by folio number", description = "Retrieve refunds by folio number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Refund>>> getRefundsByFolioNo(@PathVariable String folioNo) {
        try {
            List<Refund> refunds = refundService.getRefundsByFolioNo(folioNo);
            return ResponseEntity.ok(ApiResponse.success(refunds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve refunds: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{refundId}")
    @Operation(summary = "Update refund", description = "Update an existing refund")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Refund>> updateRefund(@PathVariable String refundId, @Valid @RequestBody Refund refundDetails) {
        try {
            Refund updatedRefund = refundService.updateRefund(refundId, refundDetails);
            return ResponseEntity.ok(ApiResponse.success("Refund updated successfully", updatedRefund));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update refund: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{refundId}")
    @Operation(summary = "Delete refund", description = "Delete a refund by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRefund(@PathVariable String refundId) {
        try {
            refundService.deleteRefund(refundId);
            return ResponseEntity.ok(ApiResponse.success("Refund deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete refund: " + e.getMessage()));
        }
    }
}