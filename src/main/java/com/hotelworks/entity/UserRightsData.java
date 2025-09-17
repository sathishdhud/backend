package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_rights_data")
public class UserRightsData {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @NotBlank
    @Column(name = "module_name", nullable = false)
    private String moduleName;
    
    @NotBlank
    @Column(name = "permission_type", nullable = false)
    private String permissionType; // read/write/none
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private HotelSoftUser hotelSoftUser;
    
    // Constructors
    public UserRightsData() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    
    public HotelSoftUser getHotelSoftUser() { return hotelSoftUser; }
    public void setHotelSoftUser(HotelSoftUser hotelSoftUser) { this.hotelSoftUser = hotelSoftUser; }
}