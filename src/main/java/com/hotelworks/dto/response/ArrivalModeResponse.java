package com.hotelworks.dto.response;

public class ArrivalModeResponse {
    
    private String id;
    private String arrivalMode;
    
    // Constructors
    public ArrivalModeResponse() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getArrivalMode() { return arrivalMode; }
    public void setArrivalMode(String arrivalMode) { this.arrivalMode = arrivalMode; }
}