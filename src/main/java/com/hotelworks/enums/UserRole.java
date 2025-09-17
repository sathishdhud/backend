package com.hotelworks.enums;

/**
 * User roles in the hotel management system
 */
public enum UserRole {
    MANAGER("Manager", "MANAGER"),
    CASHIER("Cashier", "CASHIER"),
    RECEPTIONIST("Receptionist", "RECEPTIONIST"),
    ADMIN("Admin", "ADMIN"),
    HOUSEKEEPING_STAFF("Housekeeping Staff", "HOUSEKEEPING");
    
    private final String displayName;
    private final String roleCode;
    
    UserRole(String displayName, String roleCode) {
        this.displayName = displayName;
        this.roleCode = roleCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getRoleCode() {
        return roleCode;
    }
    
    public static UserRole fromRoleCode(String roleCode) {
        for (UserRole role : values()) {
            if (role.getRoleCode().equals(roleCode)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + roleCode);
    }
}