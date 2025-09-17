package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class UserCreateRequest {
    
    private String userId; // Optional - auto-generated if not provided
    
    @NotBlank(message = "User name cannot be blank")
    private String userName;
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotBlank(message = "User type ID cannot be blank")
    private String userTypeId;
    
    private String userTypeRole; // Optional role description
    
    private List<ModulePermission> permissions;
    
    // Constructors
    public UserCreateRequest() {}
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getUserTypeId() { return userTypeId; }
    public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    
    public String getUserTypeRole() { return userTypeRole; }
    public void setUserTypeRole(String userTypeRole) { this.userTypeRole = userTypeRole; }
    
    public List<ModulePermission> getPermissions() { return permissions; }
    public void setPermissions(List<ModulePermission> permissions) { this.permissions = permissions; }
    
    public static class ModulePermission {
        private String moduleName;
        private String permissionType; // read/write/none
        
        public ModulePermission() {}
        
        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        
        public String getPermissionType() { return permissionType; }
        public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    }
}