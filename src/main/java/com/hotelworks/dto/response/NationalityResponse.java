package com.hotelworks.dto.response;

public class NationalityResponse {
    
    private String id;
    private String nationality;
    
    // Constructors
    public NationalityResponse() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}