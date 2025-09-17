package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PaymentModeRequest {
    
    @NotBlank(message = "Payment mode ID is required")
    private String id;
    
    @NotBlank(message = "Payment mode name is required")
    private String name;
    
    // Constructors
    public PaymentModeRequest() {}
    
    public PaymentModeRequest(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}