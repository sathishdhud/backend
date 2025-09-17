package com.hotelworks.interceptor;

import com.hotelworks.enums.ModuleName;
import com.hotelworks.enums.UserRole;
import com.hotelworks.service.JwtService;
import com.hotelworks.service.RolePermissionTemplateService;
import com.hotelworks.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleBasedAccessInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private RolePermissionTemplateService rolePermissionTemplateService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String authHeader = request.getHeader("Authorization");
        
        // Allow public endpoints to pass through
        if (isPublicEndpoint(request)) {
            return true;
        }
        
        // Check if token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authorization token required\"}");
            return false;
        }
        
        String token = authHeader.substring(7);
        
        // Check if token is blacklisted
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token has been invalidated\"}");
            return false;
        }
        
        try {
            // Extract user information from token
            String username = jwtService.extractUsername(token);
            String userTypeId = jwtService.extractUserTypeId(token);
            String userTypeRole = jwtService.extractUserTypeRole(token);
            
            // Validate token
            if (!jwtService.validateToken(token, username)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return false;
            }
            
            // Determine user role - first try userTypeRole from token, fallback to userTypeId if not set
            UserRole userRole = null;
            
            if (userTypeRole != null && !userTypeRole.trim().isEmpty()) {
                // Use userTypeRole field if available
                String roleIdentifier = userTypeRole.toUpperCase().replace(" ", "_");
                try {
                    // Try to map userTypeRole to enum values
                    switch (roleIdentifier) {
                        case "MANAGER":
                            userRole = UserRole.MANAGER;
                            break;
                        case "CASHIER":
                            userRole = UserRole.CASHIER;
                            break;
                        case "RECEPTIONIST":
                            userRole = UserRole.RECEPTIONIST;
                            break;
                        case "ADMIN":
                            userRole = UserRole.ADMIN;
                            break;
                        case "HOUSEKEEPING_STAFF":
                        case "HOUSEKEEPING":
                            userRole = UserRole.HOUSEKEEPING_STAFF;
                            break;
                        default:
                            // If we can't map directly, try to find a match
                            for (UserRole role : UserRole.values()) {
                                if (role.getDisplayName().equalsIgnoreCase(userTypeRole) ||
                                    role.getRoleCode().equalsIgnoreCase(userTypeRole)) {
                                    userRole = role;
                                    break;
                                }
                            }
                            break;
                    }
                } catch (Exception e) {
                    // Continue with fallback approach
                }
            }
            
            // Fallback to userTypeId if userTypeRole is not set or couldn't be mapped
            if (userRole == null) {
                try {
                    userRole = UserRole.fromRoleCode(userTypeId);
                } catch (IllegalArgumentException e) {
                    // If the userTypeId doesn't match any enum value, we still allow access
                    // This supports dynamic roles from the database
                    // For now, we'll default to a basic role or deny access based on requirements
                    // In a production system, you might want to check database roles here
                }
            }
            
            // If we still couldn't determine the role, deny access
            if (userRole == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Invalid user role\"}");
                return false;
            }
            
            // Check if user has access to this endpoint
            if (!hasAccessToEndpoint(userRole, request)) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    String errorJson = String.format(
        "{\"error\":\"Access Denied - insufficient permissions for this operation\",\"role\":\"%s\"}",
        userRole.name()
    );

    response.getWriter().write(errorJson);
    return false;
}

              
            
            return true;
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return false;
        }
    }
    
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Public endpoints that don't require authentication
        if (path.equals("/api/users/login") || path.equals("/api/users/logout") ||
            path.equals("/api/auth/login") || path.equals("/api/auth/logout") ||
            path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
            path.startsWith("/actuator") || path.equals("/api/user-types")) {
            return true;
        }
        
        return false;
    }
    
    private boolean hasAccessToEndpoint(UserRole userRole, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Extract module from endpoint path
        String module = extractModuleFromPath(path);
        if (module == null) {
            // If we can't determine the module, allow access for ADMIN, deny for others
            return userRole == UserRole.ADMIN;
        }
        
        // Check if the role has access to this module with the required permission level
        return rolePermissionTemplateService.hasEndpointAccess(userRole, path, method);
    }
    
    private String extractModuleFromPath(String path) {
        if (path.startsWith("/api/")) {
            String modulePath = path.substring(5); // Remove "/api/"
            int nextSlash = modulePath.indexOf('/');
            if (nextSlash > 0) {
                return modulePath.substring(0, nextSlash);
            }
            return modulePath;
        }
        return null;
    }
}