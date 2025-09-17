package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "ref_mode")
public class RefMode {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "ref_mode", nullable = false)
    private String refMode;
    
    // Constructors
    public RefMode() {}
    
    public RefMode(String id, String refMode) {
        this.id = id;
        this.refMode = refMode;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRefMode() { return refMode; }
    public void setRefMode(String refMode) { this.refMode = refMode; }
}