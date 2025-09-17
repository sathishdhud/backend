package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefModeRequest {
    
    private String id; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Reference mode cannot be blank")
    private String refMode;
    
    // Constructors
    public RefModeRequest() {}
    
    public RefModeRequest(String id, String refMode) {
        this.id = id;
        this.refMode = refMode;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRefMode() { return refMode; }
    public void setRefMode(String refMode) { this.refMode = refMode; }
}