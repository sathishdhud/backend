package com.hotelworks.dto.response;

public class UserTypeResponse {
    
    private String userTypeId;
    private String typeName;
    
    // Constructors
    public UserTypeResponse() {}
    
    // Getters and Setters
    public String getUserTypeId() { return userTypeId; }
    public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}