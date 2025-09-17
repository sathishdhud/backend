package com.hotelworks.dto.response;

public class ResvSourceResponse {
    
    private String id;
    private String resvSource;
    
    // Constructors
    public ResvSourceResponse() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getResvSource() { return resvSource; }
    public void setResvSource(String resvSource) { this.resvSource = resvSource; }
}