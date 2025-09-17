package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_type")
public class UserType {
    
    @Id
    @Column(name = "user_type_id")
    private String userTypeId;
    
    @NotBlank
    @Column(name = "type_name", nullable = false)
    private String typeName;
    
    // Constructors
    public UserType() {}
    
    public UserType(String userTypeId, String typeName) {
        this.userTypeId = userTypeId;
        this.typeName = typeName;
    }
    
    // Getters and Setters
    public String getUserTypeId() { return userTypeId; }
    public void setUserTypeId(String userTypeId) { this.userTypeId = userTypeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}