package com.hotelworks.service;

import com.hotelworks.dto.request.UserTypeRequest;
import com.hotelworks.dto.response.UserTypeResponse;
import com.hotelworks.entity.UserType;
import com.hotelworks.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserTypeService {
    
    @Autowired
    private UserTypeRepository userTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new user type
     */
    public UserTypeResponse createUserType(UserTypeRequest request) {
        // Generate user type ID if not provided
        String userTypeId = request.getUserTypeId();
        if (userTypeId == null || userTypeId.trim().isEmpty()) {
            userTypeId = numberGenerationService.generateUserTypeId();
        }
        
        // Check if user type already exists by ID
        if (userTypeRepository.existsById(userTypeId)) {
            throw new RuntimeException("User type ID already exists: " + userTypeId);
        }
        
        // Check if user type already exists by name
        if (userTypeRepository.existsByTypeName(request.getTypeName())) {
            throw new RuntimeException("User type name already exists: " + request.getTypeName());
        }
        
        UserType userType = new UserType();
        userType.setUserTypeId(userTypeId);
        userType.setTypeName(request.getTypeName());
        
        UserType savedUserType = userTypeRepository.save(userType);
        
        return mapToUserTypeResponse(savedUserType);
    }
    
    /**
     * Get all user types
     */
    public List<UserTypeResponse> getAllUserTypes() {
        List<UserType> userTypes = userTypeRepository.findAll();
        return userTypes.stream()
            .map(this::mapToUserTypeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user type by ID
     */
    public UserTypeResponse getUserType(String userTypeId) {
        UserType userType = userTypeRepository.findById(userTypeId)
            .orElseThrow(() -> new RuntimeException("User type not found: " + userTypeId));
        return mapToUserTypeResponse(userType);
    }
    
    /**
     * Update user type
     */
    public UserTypeResponse updateUserType(String userTypeId, UserTypeRequest request) {
        UserType userType = userTypeRepository.findById(userTypeId)
            .orElseThrow(() -> new RuntimeException("User type not found: " + userTypeId));
        
        // Check if another user type already exists with the same name
        UserType existingUserType = userTypeRepository.findByTypeName(request.getTypeName()).orElse(null);
        if (existingUserType != null && !existingUserType.getUserTypeId().equals(userTypeId)) {
            throw new RuntimeException("User type name already exists: " + request.getTypeName());
        }
        
        userType.setTypeName(request.getTypeName());
        
        UserType updatedUserType = userTypeRepository.save(userType);
        
        return mapToUserTypeResponse(updatedUserType);
    }
    
    /**
     * Delete user type
     */
    public void deleteUserType(String userTypeId) {
        if (!userTypeRepository.existsById(userTypeId)) {
            throw new RuntimeException("User type not found: " + userTypeId);
        }
        userTypeRepository.deleteById(userTypeId);
    }
    
    private UserTypeResponse mapToUserTypeResponse(UserType userType) {
        UserTypeResponse response = new UserTypeResponse();
        response.setUserTypeId(userType.getUserTypeId());
        response.setTypeName(userType.getTypeName());
        return response;
    }
}