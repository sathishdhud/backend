package com.hotelworks.dto.response;

import java.util.List;

public class UserLoginResponse {
    
    private String userId;
    private String userName;
    private String userTypeId;
    private String userTypeRole;
    private String userTypeName;
    private List<ModulePermission> permissions;
    private boolean loginSuccess;
    private String token; // JWT token for session management
    
    // Constructors
    public UserLoginResponse() {}
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserTypeId() { return userTypeId; }
    public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    
    public String getUserTypeRole() { return userTypeRole; }
    public void setUserTypeRole(String userTypeRole) { this.userTypeRole = userTypeRole; }
    
    public String getUserTypeName() { return userTypeName; }
    public void setUserTypeName(String userTypeName) { this.userTypeName = userTypeName; }
    
    public List<ModulePermission> getPermissions() { return permissions; }
    public void setPermissions(List<ModulePermission> permissions) { this.permissions = permissions; }
    
    public boolean isLoginSuccess() { return loginSuccess; }
    public void setLoginSuccess(boolean loginSuccess) { this.loginSuccess = loginSuccess; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public static class ModulePermission {
        private String moduleName;
        private String permissionType;
        
        public ModulePermission() {}
        
        public ModulePermission(String moduleName, String permissionType) {
            this.moduleName = moduleName;
            this.permissionType = permissionType;
        }
        
        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        
        public String getPermissionType() { return permissionType; }
        public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    }
}