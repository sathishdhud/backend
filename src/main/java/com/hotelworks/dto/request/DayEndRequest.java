package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DayEndRequest {
    
    @NotBlank(message = "Confirmation is required")
    private String confirmation;

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
}