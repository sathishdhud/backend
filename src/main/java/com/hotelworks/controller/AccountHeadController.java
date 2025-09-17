package com.hotelworks.controller;

import com.hotelworks.dto.request.AccountHeadRequest;
import com.hotelworks.dto.response.AccountHeadResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.AccountHeadService;
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
@RequestMapping("/api/account-heads")
@Tag(name = "Account Heads", description = "Account Head Management APIs")
public class AccountHeadController {
    
    @Autowired
    private AccountHeadService accountHeadService;
    
    @PostMapping
    @Operation(summary = "Create account head", description = "Create a new account head")
    public ResponseEntity<ApiResponse<AccountHeadResponse>> createAccountHead(
            @Valid @RequestBody AccountHeadRequest request) {
        try {
            AccountHeadResponse response = accountHeadService.createAccountHead(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account head created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create account head: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all account heads", description = "Get all available account heads")
    public ResponseEntity<ApiResponse<List<AccountHeadResponse>>> getAllAccountHeads() {
        try {
            List<AccountHeadResponse> responses = accountHeadService.getAllAccountHeads();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get account heads: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{accountHeadId}")
    @Operation(summary = "Get account head by ID", description = "Get account head details by ID")
    public ResponseEntity<ApiResponse<AccountHeadResponse>> getAccountHead(
            @Parameter(description = "Account Head ID") @PathVariable String accountHeadId) {
        try {
            AccountHeadResponse response = accountHeadService.getAccountHead(accountHeadId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Account head not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{accountHeadId}")
    @Operation(summary = "Update account head", description = "Update account head details")
    public ResponseEntity<ApiResponse<AccountHeadResponse>> updateAccountHead(
            @Parameter(description = "Account Head ID") @PathVariable String accountHeadId,
            @Valid @RequestBody AccountHeadRequest request) {
        try {
            AccountHeadResponse response = accountHeadService.updateAccountHead(accountHeadId, request);
            return ResponseEntity.ok(ApiResponse.success("Account head updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update account head: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{accountHeadId}")
    @Operation(summary = "Delete account head", description = "Delete account head by ID")
    public ResponseEntity<ApiResponse<String>> deleteAccountHead(
            @Parameter(description = "Account Head ID") @PathVariable String accountHeadId) {
        try {
            accountHeadService.deleteAccountHead(accountHeadId);
            return ResponseEntity.ok(ApiResponse.success("Account head deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete account head: " + e.getMessage()));
        }
    }
}