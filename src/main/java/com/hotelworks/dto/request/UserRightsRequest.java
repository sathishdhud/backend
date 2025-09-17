package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class UserRightsRequest {
    
    private List<ModulePermission> permissions;
    
    // Constructors
    public UserRightsRequest() {}
    
    // Getters and Setters
    public List<ModulePermission> getPermissions() { return permissions; }
    public void setPermissions(List<ModulePermission> permissions) { this.permissions = permissions; }
    
    public static class ModulePermission {
        @NotBlank(message = "Module name cannot be blank")
        private String moduleName;
        
        @NotBlank(message = "Permission type cannot be blank")
        private String permissionType; // read/write/none
        
        public ModulePermission() {}
        
        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        
        public String getPermissionType() { return permissionType; }
        public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    }
}