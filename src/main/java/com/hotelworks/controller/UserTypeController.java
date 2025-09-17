package com.hotelworks.controller;

import com.hotelworks.dto.request.UserTypeRequest;
import com.hotelworks.dto.response.UserTypeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.UserTypeService;
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
@RequestMapping("/api/user-types")
@Tag(name = "User Types", description = "User Type Management APIs")
public class UserTypeController {
    
    @Autowired
    private UserTypeService userTypeService;
    
    @PostMapping
    @Operation(summary = "Create user type", description = "Create a new user type")
    public ResponseEntity<ApiResponse<UserTypeResponse>> createUserType(
            @Valid @RequestBody UserTypeRequest request) {
        try {
            UserTypeResponse response = userTypeService.createUserType(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User type created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create user type: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all user types", description = "Get all available user types")
    public ResponseEntity<ApiResponse<List<UserTypeResponse>>> getAllUserTypes() {
        try {
            List<UserTypeResponse> responses = userTypeService.getAllUserTypes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get user types: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{userTypeId}")
    @Operation(summary = "Get user type by ID", description = "Get user type details by ID")
    public ResponseEntity<ApiResponse<UserTypeResponse>> getUserType(
            @Parameter(description = "User Type ID") @PathVariable String userTypeId) {
        try {
            UserTypeResponse response = userTypeService.getUserType(userTypeId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User type not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{userTypeId}")
    @Operation(summary = "Update user type", description = "Update user type details")
    public ResponseEntity<ApiResponse<UserTypeResponse>> updateUserType(
            @Parameter(description = "User Type ID") @PathVariable String userTypeId,
            @Valid @RequestBody UserTypeRequest request) {
        try {
            UserTypeResponse response = userTypeService.updateUserType(userTypeId, request);
            return ResponseEntity.ok(ApiResponse.success("User type updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user type: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{userTypeId}")
    @Operation(summary = "Delete user type", description = "Delete user type by ID")
    public ResponseEntity<ApiResponse<String>> deleteUserType(
            @Parameter(description = "User Type ID") @PathVariable String userTypeId) {
        try {
            userTypeService.deleteUserType(userTypeId);
            return ResponseEntity.ok(ApiResponse.success("User type deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete user type: " + e.getMessage()));
        }
    }
}