package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ResvSourceRequest {
    
    private String id; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Reservation source cannot be blank")
    private String resvSource;
    
    // Constructors
    public ResvSourceRequest() {}
    
    public ResvSourceRequest(String id, String resvSource) {
        this.id = id;
        this.resvSource = resvSource;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getResvSource() { return resvSource; }
    public void setResvSource(String resvSource) { this.resvSource = resvSource; }
}