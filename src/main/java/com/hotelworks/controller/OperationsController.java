package com.hotelworks.controller;

import com.hotelworks.dto.request.AuditDateChangeRequest;
import com.hotelworks.dto.request.ShiftChangeRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.OperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operations")
@Tag(name = "Operations", description = "Hotel Operations Management APIs")
public class OperationsController {
    
    @Autowired
    private OperationsService operationsService;
    
    @PostMapping("/audit-date-change")
    @Operation(summary = "Process audit date change", 
               description = "Process audit date change - posts room charges and taxes for all in-house guests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> processAuditDateChange(
            @Valid @RequestBody AuditDateChangeRequest request) {
        try {
            String result = operationsService.processAuditDateChange(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to process audit date change: " + e.getMessage()));
        }
    }
    
    @PostMapping("/shift-change")
    @Operation(summary = "Process shift change", 
               description = "Process shift change - updates shift table with balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> processShiftChange(
            @Valid @RequestBody ShiftChangeRequest request) {
        try {
            String result = operationsService.processShiftChange(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to process shift change: " + e.getMessage()));
        }
    }
}