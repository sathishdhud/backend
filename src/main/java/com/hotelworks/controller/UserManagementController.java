package com.hotelworks.controller;

import com.hotelworks.dto.request.UserCreateRequest;
import com.hotelworks.dto.request.UserLoginRequest;
import com.hotelworks.dto.request.UserRightsRequest;
import com.hotelworks.dto.response.UserResponse;
import com.hotelworks.dto.response.UserLoginResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.UserManagementService;
import com.hotelworks.enums.UserRole;
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
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User Management and Authentication APIs")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token with user details and permissions")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = userManagementService.authenticateUser(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate JWT token")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            userManagementService.logoutUser(authorizationHeader);
            return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user with permissions")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse response = userManagementService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get all system users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            List<UserResponse> responses = userManagementService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user details by user ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        try {
            UserResponse response = userManagementService.getUser(userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}/rights")
    @Operation(summary = "Update user rights", description = "Update user permissions for modules")
    public ResponseEntity<ApiResponse<String>> updateUserRights(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Valid @RequestBody UserRightsRequest request) {
        try {
            userManagementService.updateUserRights(userId, request);
            return ResponseEntity.ok(ApiResponse.success("User rights updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user rights: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile", description = "Update user profile information (username, name, user type)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse response = userManagementService.updateUser(userId, request);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete user by ID")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        try {
            userManagementService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
    
    // Role-based management endpoints
    
    @GetMapping("/roles")
    @Operation(summary = "Get available roles", description = "Get all available user roles with descriptions")
    public ResponseEntity<ApiResponse<List<UserManagementService.RoleInfo>>> getAvailableRoles() {
        try {
            List<UserManagementService.RoleInfo> roles = userManagementService.getAvailableRoles();
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get roles: " + e.getMessage()));
        }
    }
    
    @PostMapping("/create-with-role")
    @Operation(summary = "Create user with role", description = "Create a new user with predefined role permissions")
    public ResponseEntity<ApiResponse<UserResponse>> createUserWithRole(
            @Parameter(description = "Username") @RequestParam String userName,
            @Parameter(description = "Password") @RequestParam String password,
            @Parameter(description = "User role") @RequestParam UserRole role) {
        try {
            UserResponse response = userManagementService.createUserWithRole(userName, password, role);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created with role successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create user with role: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}/role")
    @Operation(summary = "Update user role", description = "Update user role and reset permissions to role defaults")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "New user role") @RequestParam UserRole role) {
        try {
            UserResponse response = userManagementService.updateUserRole(userId, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user role: " + e.getMessage()));
        }
    }
    
    @GetMapping("/by-role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users with specific role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable String role) {
        try {
            List<UserResponse> users = userManagementService.getUsersByUserTypeName(role);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get users by role: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}/permissions/{module}")
    @Operation(summary = "Check user permission", description = "Check if user has specific module permission")
    public ResponseEntity<ApiResponse<Boolean>> checkUserPermission(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Module name") @PathVariable String module,
            @Parameter(description = "Permission type (read/write/full)") @RequestParam String permissionType) {
        try {
            boolean hasPermission = userManagementService.hasModulePermission(userId, module, permissionType);
            return ResponseEntity.ok(ApiResponse.success("Permission check completed", hasPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to check permission: " + e.getMessage()));
        }
    }
}