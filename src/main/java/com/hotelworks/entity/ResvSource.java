package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "resv_source")
public class ResvSource {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "resv_source", nullable = false)
    private String resvSource;
    
    // Constructors
    public ResvSource() {}
    
    public ResvSource(String id, String resvSource) {
        this.id = id;
        this.resvSource = resvSource;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getResvSource() { return resvSource; }
    public void setResvSource(String resvSource) { this.resvSource = resvSource; }
}