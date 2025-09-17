package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ArrivalModeRequest {
    
    private String id; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Arrival mode cannot be blank")
    private String arrivalMode;
    
    // Constructors
    public ArrivalModeRequest() {}
    
    public ArrivalModeRequest(String id, String arrivalMode) {
        this.id = id;
        this.arrivalMode = arrivalMode;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getArrivalMode() { return arrivalMode; }
    public void setArrivalMode(String arrivalMode) { this.arrivalMode = arrivalMode; }
}