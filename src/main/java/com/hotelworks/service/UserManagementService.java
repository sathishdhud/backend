package com.hotelworks.service;

import com.hotelworks.dto.request.*;
import com.hotelworks.dto.response.*;
import com.hotelworks.entity.*;
import com.hotelworks.repository.*;
import com.hotelworks.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserManagementService {
    
    @Autowired
    private HotelSoftUserRepository hotelSoftUserRepository;
    
    @Autowired
    private UserRightsDataRepository userRightsDataRepository;
    
    @Autowired
    private UserTypeRepository userTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private RolePermissionTemplateService rolePermissionTemplateService;
    
    /**
     * Authenticate user login and generate JWT token
     */
    public UserLoginResponse authenticateUser(UserLoginRequest request) {
        HotelSoftUser user = hotelSoftUserRepository.findByUserName(request.getUserName())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        // In production, use proper password hashing (BCrypt, etc.)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        // Generate JWT token with userTypeRole
        String token = jwtService.generateToken(
            user.getUserId(), 
            user.getUserName(), 
            user.getUserTypeId(), 
            user.getUserTypeRole()
        );
        
        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserTypeId(user.getUserTypeId());
        response.setUserTypeRole(user.getUserTypeRole());
        response.setLoginSuccess(true);
        response.setToken(token);
        
        // Get user type name
        userTypeRepository.findById(user.getUserTypeId())
            .ifPresent(userType -> response.setUserTypeName(userType.getTypeName()));
        
        // Get user permissions
        List<UserRightsData> rights = userRightsDataRepository.findByUserId(user.getUserId());
        List<UserLoginResponse.ModulePermission> permissions = rights.stream()
            .map(right -> new UserLoginResponse.ModulePermission(right.getModuleName(), right.getPermissionType()))
            .collect(Collectors.toList());
        response.setPermissions(permissions);
        
        return response;
    }
    
    /**
     * Logout user by blacklisting token
     */
    public void logoutUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            tokenBlacklistService.blacklistToken(jwt);
        }
    }
    
    /**
     * Create a new user
     */
    public UserResponse createUser(UserCreateRequest request) {
        // Generate user ID if not provided
        String userId = request.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            userId = numberGenerationService.generateUserId();
        }
        
        // Check if user already exists
        if (hotelSoftUserRepository.existsById(userId)) {
            throw new RuntimeException("User already exists: " + userId);
        }
        
        // Check if username already exists
        if (hotelSoftUserRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.getUserName());
        }
        
        // Validate user type
        if (!userTypeRepository.existsById(request.getUserTypeId())) {
            throw new RuntimeException("User type not found: " + request.getUserTypeId());
        }
        
        // Create user
        HotelSoftUser user = new HotelSoftUser();
        user.setUserId(userId);
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword()); // In production, hash the password
        user.setUserTypeId(request.getUserTypeId());
        user.setUserTypeRole(request.getUserTypeRole());
        
        HotelSoftUser savedUser = hotelSoftUserRepository.save(user);
        
        // Create user rights
        if (request.getPermissions() != null) {
            createUserRights(userId, request.getPermissions());
        }
        
        return mapToUserResponse(savedUser);
    }
    
    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        List<HotelSoftUser> users = hotelSoftUserRepository.findAll();
        return users.stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user by ID
     */
    public UserResponse getUser(String userId) {
        HotelSoftUser user = hotelSoftUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return mapToUserResponse(user);
    }
    
    /**
     * Update user rights
     */
    public void updateUserRights(String userId, UserRightsRequest request) {
        // Check if user exists
        if (!hotelSoftUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }
        
        // Delete existing rights
        userRightsDataRepository.deleteByUserId(userId);
        
        // Create new rights
        if (request.getPermissions() != null) {
            createUserRights(userId, request.getPermissions().stream()
                .map(p -> {
                    UserCreateRequest.ModulePermission mp = new UserCreateRequest.ModulePermission();
                    mp.setModuleName(p.getModuleName());
                    mp.setPermissionType(p.getPermissionType());
                    return mp;
                })
                .collect(Collectors.toList()));
        }
    }
    
    /**
     * Delete user
     */
    public void deleteUser(String userId) {
        if (!hotelSoftUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }
        
        // Delete user rights first
        userRightsDataRepository.deleteByUserId(userId);
        
        // Delete user
        hotelSoftUserRepository.deleteById(userId);
    }
    
    /**
     * Update user profile
     */
    public UserResponse updateUser(String userId, UserCreateRequest request) {
        HotelSoftUser user = hotelSoftUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Check if new username conflicts with existing user
        if (!user.getUserName().equals(request.getUserName())) {
            if (hotelSoftUserRepository.findByUserName(request.getUserName()).isPresent()) {
                throw new RuntimeException("Username already exists: " + request.getUserName());
            }
            user.setUserName(request.getUserName());
        }
        
        // Validate user type if changed
        if (!user.getUserTypeId().equals(request.getUserTypeId())) {
            if (!userTypeRepository.existsById(request.getUserTypeId())) {
                throw new RuntimeException("User type not found: " + request.getUserTypeId());
            }
            user.setUserTypeId(request.getUserTypeId());
        }
        
        // Update user type role if provided
        if (request.getUserTypeRole() != null) {
            user.setUserTypeRole(request.getUserTypeRole());
        }
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(request.getPassword()); // In production, hash the password
        }
        
        HotelSoftUser savedUser = hotelSoftUserRepository.save(user);
        
        // Update user rights if provided
        if (request.getPermissions() != null) {
            // Delete existing rights
            userRightsDataRepository.deleteByUserId(userId);
            
            // Create new rights
            createUserRights(userId, request.getPermissions());
        }
        
        return mapToUserResponse(savedUser);
    }
    
    /**
     * Create user with predefined role
     */
    public UserResponse createUserWithRole(String userName, String password, UserRole role) {
        // Generate user ID
        String userId = numberGenerationService.generateUserId();
        
        // Check if username already exists
        if (hotelSoftUserRepository.findByUserName(userName).isPresent()) {
            throw new RuntimeException("Username already exists: " + userName);
        }
        
        // Get or create user type for role
        String userTypeId = ensureUserTypeExists(role);
        
        // Create user
        HotelSoftUser user = new HotelSoftUser();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(password); // In production, hash the password
        user.setUserTypeId(userTypeId);
        user.setUserTypeRole(role.getDisplayName()); // Set role as description
        
        HotelSoftUser savedUser = hotelSoftUserRepository.save(user);
        
        // Create default permissions for role
        List<UserCreateRequest.ModulePermission> permissions = rolePermissionTemplateService.getDefaultPermissions(role);
        createUserRights(userId, permissions);
        
        return mapToUserResponse(savedUser);
    }
    
    /**
     * Update user role and permissions
     */
    public UserResponse updateUserRole(String userId, UserRole role) {
        HotelSoftUser user = hotelSoftUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Get or create user type for role
        String userTypeId = ensureUserTypeExists(role);
        user.setUserTypeId(userTypeId);
        user.setUserTypeRole(role.getDisplayName()); // Update role description
        
        HotelSoftUser savedUser = hotelSoftUserRepository.save(user);
        
        // Delete existing rights
        userRightsDataRepository.deleteByUserId(userId);
        
        // Create new rights based on role
        List<UserCreateRequest.ModulePermission> permissions = rolePermissionTemplateService.getDefaultPermissions(role);
        createUserRights(userId, permissions);
        
        return mapToUserResponse(savedUser);
    }
    
    /**
     * Get all available roles including dynamic roles from database
     */
    public List<RoleInfo> getAvailableRoles() {
        // Get all user types from database
        List<UserType> userTypes = userTypeRepository.findAll();
        List<RoleInfo> roles = new ArrayList<>();
        
        for (UserType userType : userTypes) {
            RoleInfo roleInfo = new RoleInfo();
            roleInfo.setRoleCode(userType.getUserTypeId());
            roleInfo.setDisplayName(userType.getTypeName());
            roleInfo.setDescription(getRoleDescription(userType.getUserTypeId()));
            roleInfo.setUserTypeId(userType.getUserTypeId());
            roles.add(roleInfo);
        }
        
        return roles;
    }
    
    /**
     * Get role description based on role code
     * Falls back to default descriptions for standard roles
     */
    private String getRoleDescription(String roleCode) {
        try {
            UserRole enumRole = UserRole.fromRoleCode(roleCode);
            return rolePermissionTemplateService.getRoleDescription(enumRole);
        } catch (IllegalArgumentException e) {
            // For custom roles not in the enum, provide a generic description
            return "Custom user role";
        }
    }
    
    /**
     * Get users by role
     */
    public List<UserResponse> getUsersByRole(UserRole role) {
        String userTypeId = role.getRoleCode();
        List<HotelSoftUser> users = hotelSoftUserRepository.findByUserTypeId(userTypeId);
        return users.stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get users by role code (supports both enum and custom database roles)
     */
    public List<UserResponse> getUsersByRoleCode(String roleCode) {
        List<HotelSoftUser> users = hotelSoftUserRepository.findByUserTypeId(roleCode);
        return users.stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get users by user type name
     */
    public List<UserResponse> getUsersByUserTypeName(String userTypeName) {
        // First find the user type by name (exact match)
        List<UserType> userTypes = userTypeRepository.findByTypeNameIgnoreCase(userTypeName);
        if (userTypes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get users for all matching user types
        List<UserResponse> allUsers = new ArrayList<>();
        for (UserType userType : userTypes) {
            List<HotelSoftUser> users = hotelSoftUserRepository.findByUserTypeId(userType.getUserTypeId());
            List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
            allUsers.addAll(userResponses);
        }
        
        return allUsers;
    }
    
    /**
     * Check if user has permission for module
     */
    public boolean hasModulePermission(String userId, String moduleName, String permissionType) {
        List<UserRightsData> rights = userRightsDataRepository.findByUserId(userId);
        
        for (UserRightsData right : rights) {
            if (right.getModuleName().equals(moduleName)) {
                String userPermission = right.getPermissionType();
                
                // Check permission hierarchy: full > write > read > none
                if ("full".equals(userPermission)) {
                    return true;
                } else if ("write".equals(userPermission)) {
                    return "write".equals(permissionType) || "read".equals(permissionType);
                } else if ("read".equals(userPermission)) {
                    return "read".equals(permissionType);
                }
            }
        }
        
        return false;
    }
    
    private String ensureUserTypeExists(UserRole role) {
        String userTypeId = role.getRoleCode();
        
        if (!userTypeRepository.existsById(userTypeId)) {
            // Create user type if it doesn't exist
            UserType userType = new UserType();
            userType.setUserTypeId(userTypeId);
            userType.setTypeName(role.getDisplayName());
            userTypeRepository.save(userType);
        }
        
        return userTypeId;
    }
    
    /**
     * Ensure user type exists in database based on userTypeId
     * This method supports both enum roles and custom database roles
     */
    private String ensureUserTypeExists(String userTypeId, String typeName) {
        if (!userTypeRepository.existsById(userTypeId)) {
            // Create user type if it doesn't exist
            UserType userType = new UserType();
            userType.setUserTypeId(userTypeId);
            userType.setTypeName(typeName != null ? typeName : userTypeId);
            userTypeRepository.save(userType);
        }
        
        return userTypeId;
    }
    
    private void createUserRights(String userId, List<UserCreateRequest.ModulePermission> permissions) {
        for (UserCreateRequest.ModulePermission permission : permissions) {
            UserRightsData rightsData = new UserRightsData();
            rightsData.setId(numberGenerationService.generateUserRightsId());
            rightsData.setUserId(userId);
            rightsData.setModuleName(permission.getModuleName());
            rightsData.setPermissionType(permission.getPermissionType());
            userRightsDataRepository.save(rightsData);
        }
    }
    
    private UserResponse mapToUserResponse(HotelSoftUser user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserTypeId(user.getUserTypeId());
        response.setUserTypeRole(user.getUserTypeRole());
        
        // Get user type name
        userTypeRepository.findById(user.getUserTypeId())
            .ifPresent(userType -> response.setUserTypeName(userType.getTypeName()));
        
        // Get user permissions
        List<UserRightsData> rights = userRightsDataRepository.findByUserId(user.getUserId());
        List<UserResponse.ModulePermission> permissions = rights.stream()
            .map(right -> new UserResponse.ModulePermission(right.getModuleName(), right.getPermissionType()))
            .collect(Collectors.toList());
        response.setPermissions(permissions);
        
        return response;
    }
    
    /**
     * Role information DTO
     */
    public static class RoleInfo {
        private String roleCode;
        private String displayName;
        private String description;
        private String userTypeId;
        
        // Getters and Setters
        public String getRoleCode() { return roleCode; }
        public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUserTypeId() { return userTypeId; }
        public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    }
}