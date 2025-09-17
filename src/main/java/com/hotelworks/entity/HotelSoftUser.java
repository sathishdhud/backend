package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "hotelsoftusers")
public class HotelSoftUser {
    
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @NotBlank
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;
    
    @NotBlank
    @Column(name = "user_type_id", nullable = false)
    private String userTypeId;
    
    @Column(name = "user_type_role")
    private String userTypeRole;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", insertable = false, updatable = false)
    private UserType userType;
    
    // Constructors
    public HotelSoftUser() {}
    
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
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
}