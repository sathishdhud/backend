package com.hotelworks.enums;

/**
 * Permission types for module access
 */
public enum PermissionType {
    READ("read"),
    WRITE("write"),
    FULL("full"),
    NONE("none");
    
    private final String permission;
    
    PermissionType(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public static PermissionType fromString(String permission) {
        for (PermissionType type : values()) {
            if (type.getPermission().equals(permission)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown permission type: " + permission);
    }
}