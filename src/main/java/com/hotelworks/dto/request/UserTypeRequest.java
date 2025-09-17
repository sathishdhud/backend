package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserTypeRequest {
    
    private String userTypeId; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Type name cannot be blank")
    private String typeName;
    
    // Constructors
    public UserTypeRequest() {}
    
    public UserTypeRequest(String userTypeId, String typeName) {
        this.userTypeId = userTypeId;
        this.typeName = typeName;
    }
    
    // Getters and Setters
    public String getUserTypeId() { return userTypeId; }
    public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}