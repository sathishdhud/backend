package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class NationalityRequest {
    
    private String id; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Nationality cannot be blank")
    private String nationality;
    
    // Constructors
    public NationalityRequest() {}
    
    public NationalityRequest(String id, String nationality) {
        this.id = id;
        this.nationality = nationality;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}