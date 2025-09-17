package com.hotelworks.security;

import com.hotelworks.service.RolePermissionTemplateService;
import com.hotelworks.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Simple role permission checker for service layer validation
 */
@Component
public class RolePermissionChecker {
    
    @Autowired
    private RolePermissionTemplateService rolePermissionTemplateService;
    
    /**
     * Check if current user has permission for a specific module
     */
    public boolean hasModulePermission(String moduleName, String permissionType) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return false;
            }
            
            
            // Extract role from authorities
            String roleCode = auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
                .findFirst()
                .orElse(null);
            
            if (roleCode == null) {
                return false;
            }
            
            // Find UserRole by code
            UserRole userRole = null;
            for (UserRole role : UserRole.values()) {
                if (role.getRoleCode().equals(roleCode)) {
                    userRole = role;
                    break;
                }
            }
            
            if (userRole == null) {
                return false;
            }
            
            // Check if role has access to module
            return rolePermissionTemplateService.getDefaultPermissions(userRole)
                .stream()
                .anyMatch(permission -> 
                    permission.getModuleName().equals(moduleName) && 
                    hasPermissionLevel(permission.getPermissionType(), permissionType)
                );
                
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roleCodes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return false;
            }
            
            String userRole = auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElse(null);
            
            if (userRole == null) {
                return false;
            }
            
            for (String roleCode : roleCodes) {
                if (roleCode.equals(userRole)) {
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get current user's role
     */
    public String getCurrentUserRole() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return null;
            }
            
            return auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElse(null);
                
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean hasPermissionLevel(String userPermission, String requiredPermission) {
        // Permission hierarchy: full > write > read > none
        switch (userPermission) {
            case "full":
                return true;
            case "write":
                return "write".equals(requiredPermission) || "read".equals(requiredPermission);
            case "read":
                return "read".equals(requiredPermission);
            case "none":
            default:
                return false;
        }
    }
}