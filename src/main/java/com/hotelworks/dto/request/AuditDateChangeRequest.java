package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AuditDateChangeRequest {
    
    @NotBlank(message = "Confirmation is required")
    private String confirmation; // "YES" to proceed
    
    // Constructors
    public AuditDateChangeRequest() {}
    
    // Getters and Setters
    public String getConfirmation() { return confirmation; }
    public void setConfirmation(String confirmation) { this.confirmation = confirmation; }
}