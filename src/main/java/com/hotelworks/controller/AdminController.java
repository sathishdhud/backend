package com.hotelworks.controller;

import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.UserResponse;
import com.hotelworks.service.UserManagementService;
import com.hotelworks.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administrative Functions APIs")
public class AdminController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard", description = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Get user statistics
            List<UserResponse> allUsers = userManagementService.getAllUsers();
            dashboard.put("totalUsers", allUsers.size());
            
            // Get role-based user counts
            Map<String, Integer> roleStats = new HashMap<>();
            for (UserRole role : UserRole.values()) {
                List<UserResponse> roleUsers = userManagementService.getUsersByRole(role);
                roleStats.put(role.getDisplayName(), roleUsers.size());
            }
            dashboard.put("usersByRole", roleStats);
            
            // Get available roles
            List<UserManagementService.RoleInfo> roles = userManagementService.getAvailableRoles();
            dashboard.put("availableRoles", roles);
            
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get dashboard data: " + e.getMessage()));
        }
    }
    
    @PostMapping("/users/bulk-create")
    @Operation(summary = "Bulk create users", description = "Create multiple users with predefined roles")
    public ResponseEntity<ApiResponse<List<UserResponse>>> bulkCreateUsers(
            @RequestBody List<BulkUserRequest> requests) {
        try {
            List<UserResponse> createdUsers = new ArrayList<>();
            
            for (BulkUserRequest request : requests) {
                UserResponse user = userManagementService.createUserWithRole(
                    request.getUserName(), 
                    request.getPassword(), 
                    request.getRole()
                );
                createdUsers.add(user);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Users created successfully", createdUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to bulk create users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/system-status")
    @Operation(summary = "System status", description = "Get system health and status information")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // Basic system information
            status.put("systemTime", java.time.LocalDateTime.now());
            status.put("status", "RUNNING");
            
            // User system status
            List<UserResponse> allUsers = userManagementService.getAllUsers();
            status.put("activeUsers", allUsers.size());
            
            // Role distribution
            Map<String, Integer> roleDistribution = new HashMap<>();
            for (UserRole role : UserRole.values()) {
                List<UserResponse> roleUsers = userManagementService.getUsersByRole(role);
                roleDistribution.put(role.getRoleCode(), roleUsers.size());
            }
            status.put("roleDistribution", roleDistribution);
            
            return ResponseEntity.ok(ApiResponse.success("System status retrieved", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get system status: " + e.getMessage()));
        }
    }
    
    // DTOs for admin operations
    public static class BulkUserRequest {
        private String userName;
        private String password;
        private UserRole role;
        
        // Getters and Setters
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }
}