package com.hotelworks.dto.response;

public class PaymentModeResponse {
    
    private String id;
    private String name;
    
    // Constructors
    public PaymentModeResponse() {}
    
    public PaymentModeResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}