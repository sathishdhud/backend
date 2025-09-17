package com.hotelworks.service;

import com.hotelworks.dto.request.UserCreateRequest;
import com.hotelworks.enums.ModuleName;
import com.hotelworks.enums.PermissionType;
import com.hotelworks.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing role-based permission templates
 */
@Service
public class RolePermissionTemplateService {
    
    /**
     * Get default permissions for a user role
     */
    public List<UserCreateRequest.ModulePermission> getDefaultPermissions(UserRole role) {
        List<UserCreateRequest.ModulePermission> permissions = new ArrayList<>();
        
        switch (role) {
            case MANAGER:
                // Manager: View all data (reservations, check-ins, payments, bills, charges, housekeeping)
                // Tasks: Approve check-outs, review financial reports, assign staff roles
                addPermission(permissions, ModuleName.RESERVATIONS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHECKINS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHECKOUTS, PermissionType.FULL); // Approve check-outs
                addPermission(permissions, ModuleName.ROOMS, PermissionType.READ);
                addPermission(permissions, ModuleName.ROOM_TYPES, PermissionType.READ);
                addPermission(permissions, ModuleName.PAYMENTS, PermissionType.READ);
                addPermission(permissions, ModuleName.ADVANCES, PermissionType.READ);
                addPermission(permissions, ModuleName.BILLS, PermissionType.READ);
                addPermission(permissions, ModuleName.TRANSACTIONS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHARGES, PermissionType.READ);
                addPermission(permissions, ModuleName.REPORTS, PermissionType.FULL); // Review financial reports
                addPermission(permissions, ModuleName.USERS, PermissionType.READ);
                addPermission(permissions, ModuleName.STAFF_MANAGEMENT, PermissionType.FULL); // Assign staff roles
                addPermission(permissions, ModuleName.HOUSEKEEPING, PermissionType.READ);
                addPermission(permissions, ModuleName.OPERATIONS, PermissionType.READ);
                addPermission(permissions, ModuleName.COMPANIES, PermissionType.READ);
                addPermission(permissions, ModuleName.PLAN_TYPES, PermissionType.READ);
                break;
                
            case CASHIER:
                // Cashier: Payments (advances, bill_settlement), bills, reports
                // Tasks: Record payments, generate financial reports, verify settlements
                addPermission(permissions, ModuleName.PAYMENTS, PermissionType.FULL);
                addPermission(permissions, ModuleName.ADVANCES, PermissionType.FULL); // POST /api/advances
                addPermission(permissions, ModuleName.BILLS, PermissionType.FULL); // POST /api/bill-settlements
                addPermission(permissions, ModuleName.TRANSACTIONS, PermissionType.READ);
                addPermission(permissions, ModuleName.REPORTS, PermissionType.FULL); // GET /api/reports/*
                addPermission(permissions, ModuleName.PAYMENT_MODES, PermissionType.READ);
                addPermission(permissions, ModuleName.ACCOUNT_HEADS, PermissionType.READ);
                addPermission(permissions, ModuleName.RESERVATIONS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHECKINS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHECKOUTS, PermissionType.READ);
                addPermission(permissions, ModuleName.ROOMS, PermissionType.READ);
                break;
                
            case RECEPTIONIST:
                // Receptionist: Reservations, check-ins, check-outs, payments, charges
                // Tasks: Record guest data, payments, and charges; perform check-outs
                addPermission(permissions, ModuleName.RESERVATIONS, PermissionType.FULL); // POST /api/reservations
                addPermission(permissions, ModuleName.CHECKINS, PermissionType.FULL); // POST /api/checkins
                addPermission(permissions, ModuleName.CHECKOUTS, PermissionType.FULL); // POST /api/checkouts
                addPermission(permissions, ModuleName.PAYMENTS, PermissionType.FULL);
                addPermission(permissions, ModuleName.ADVANCES, PermissionType.FULL); // POST /api/advances
                addPermission(permissions, ModuleName.CHARGES, PermissionType.FULL); // POST /api/charges
                addPermission(permissions, ModuleName.TRANSACTIONS, PermissionType.FULL);
                addPermission(permissions, ModuleName.ROOMS, PermissionType.READ);
                addPermission(permissions, ModuleName.ROOM_TYPES, PermissionType.READ);
                addPermission(permissions, ModuleName.BILLS, PermissionType.READ); // Only read access to bills
                addPermission(permissions, ModuleName.COMPANIES, PermissionType.READ);
                addPermission(permissions, ModuleName.PAYMENT_MODES, PermissionType.READ);
                addPermission(permissions, ModuleName.PLAN_TYPES, PermissionType.READ);
                addPermission(permissions, ModuleName.NATIONALITIES, PermissionType.READ);
                addPermission(permissions, ModuleName.ARRIVAL_MODES, PermissionType.READ);
                addPermission(permissions, ModuleName.REF_MODES, PermissionType.READ);
                addPermission(permissions, ModuleName.RESERVATION_SOURCES, PermissionType.READ);
                addPermission(permissions, ModuleName.REPORTS, PermissionType.READ);
                break;
                
            case ADMIN:
                // Admin: Full system access
                for (ModuleName module : ModuleName.values()) {
                    addPermission(permissions, module, PermissionType.FULL);
                }
                break;
                
            case HOUSEKEEPING_STAFF:
                // Housekeeping Staff: Update room status (clean, dirty)
                // Tasks: Room status management and cleaning task coordination
                addPermission(permissions, ModuleName.ROOMS, PermissionType.WRITE);
                addPermission(permissions, ModuleName.HOUSEKEEPING, PermissionType.FULL); // POST /api/housekeeping, GET /api/housekeeping
                addPermission(permissions, ModuleName.CHECKINS, PermissionType.READ);
                addPermission(permissions, ModuleName.CHECKOUTS, PermissionType.READ);
                addPermission(permissions, ModuleName.RESERVATIONS, PermissionType.READ);
                break;
        }
        
        return permissions;
    }
    
    /**
     * Get user type ID for a role
     */
    public String getUserTypeIdForRole(UserRole role) {
        return role.getRoleCode();
    }
    
    /**
     * Get role description
     */
    public String getRoleDescription(UserRole role) {
        switch (role) {
            case MANAGER:
                return "Manager with full oversight access, approval capabilities, and staff management";
            case CASHIER:
                return "Cashier with payment processing, financial reports, and settlement verification";
            case RECEPTIONIST:
                return "Receptionist with guest data management, check-in/out, and basic operations";
            case ADMIN:
                return "System administrator with full access to all modules and configurations";
            case HOUSEKEEPING_STAFF:
                return "Housekeeping staff with room status management and cleaning task coordination";
            default:
                return "Standard user role";
        }
    }
    
    /**
     * Check if a role has access to a specific endpoint
     */
    public boolean hasEndpointAccess(UserRole role, String endpoint, String method) {
        List<UserCreateRequest.ModulePermission> permissions = getDefaultPermissions(role);
        
        // Extract module from endpoint
        String module = extractModuleFromEndpoint(endpoint);
        if (module == null) {
            // Allow access to unknown modules by default for ADMIN, deny for others
            return role == UserRole.ADMIN;
        }
        
        // Special case: restrict related bills access to Cashier only
        if (endpoint.contains("/related") && module.equals("bills")) {
            return role == UserRole.CASHIER || role == UserRole.ADMIN;
        }
        
        // Find permission for module
        for (UserCreateRequest.ModulePermission permission : permissions) {
            if (permission.getModuleName().equals(module)) {
                return hasMethodAccess(permission.getPermissionType(), method);
            }
        }
        
        // If no specific permission is defined for this module, deny access except for ADMIN
        return role == UserRole.ADMIN;
    }
    
    private void addPermission(List<UserCreateRequest.ModulePermission> permissions, 
                              ModuleName module, PermissionType type) {
        UserCreateRequest.ModulePermission permission = new UserCreateRequest.ModulePermission();
        permission.setModuleName(module.getModuleName());
        permission.setPermissionType(type.getPermission());
        permissions.add(permission);
    }
    
    private String extractModuleFromEndpoint(String endpoint) {
        if (endpoint.startsWith("/api/")) {
            String path = endpoint.substring(5); // Remove "/api/"
            // Handle special cases for multi-word module names
            if (path.startsWith("user-types")) {
                return "user-types";
            } else if (path.startsWith("payment-modes")) {
                return "payment-modes";
            } else if (path.startsWith("plan-types")) {
                return "plan-types";
            } else if (path.startsWith("arrival-modes")) {
                return "arrival-modes";
            } else if (path.startsWith("ref-modes")) {
                return "ref-modes";
            } else if (path.startsWith("reservation-sources")) {
                return "reservation-sources";
            } else if (path.startsWith("account-heads")) {
                return "account-heads";
            } else if (path.startsWith("bill-settlements")) {
                return "bills"; // Map to bills module
            } else if (path.startsWith("checkouts")) {
                return "checkouts";
            } else if (path.startsWith("checkins")) {
                return "checkins";
            } else if (path.startsWith("reservations")) {
                return "reservations";
            } else if (path.startsWith("rooms")) {
                return "rooms";
            } else if (path.startsWith("room-types")) {
                return "room-types";
            } else if (path.startsWith("payments")) {
                return "payments";
            } else if (path.startsWith("advances")) {
                return "advances";
            } else if (path.startsWith("bills")) {
                return "bills";
            } else if (path.startsWith("transactions")) {
                return "transactions";
            } else if (path.startsWith("charges")) {
                return "charges";
            } else if (path.startsWith("reports")) {
                return "reports";
            } else if (path.startsWith("users")) {
                return "users";
            } else if (path.startsWith("companies")) {
                return "companies";
            } else if (path.startsWith("nationalities")) {
                return "nationalities";
            } else if (path.startsWith("taxes")) {
                return "taxes";
            } else if (path.startsWith("operations")) {
                return "operations";
            } else if (path.startsWith("housekeeping")) {
                return "housekeeping";
            } else {
                // For other cases, extract until the first slash
                int nextSlash = path.indexOf('/');
                if (nextSlash > 0) {
                    return path.substring(0, nextSlash);
                }
                return path;
            }
        }
        return null;
    }
    
    private boolean hasMethodAccess(String permissionType, String method) {
        switch (permissionType) {
            case "full":
                return true;
            case "write":
                // Write permission allows POST, PUT, DELETE
                return !"GET".equalsIgnoreCase(method);
            case "read":
                // Read permission only allows GET
                return "GET".equalsIgnoreCase(method);
            case "none":
            default:
                return false;
        }
    }
}