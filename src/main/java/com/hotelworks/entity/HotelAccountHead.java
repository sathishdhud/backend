package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "hotel_account_head")
public class HotelAccountHead {
    
    @Id
    @Column(name = "acc_head_id")
    private String accHeadId;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    // Constructors
    public HotelAccountHead() {}
    
    public HotelAccountHead(String accHeadId, String name) {
        this.accHeadId = accHeadId;
        this.name = name;
    }
    
    // Getters and Setters
    public String getAccHeadId() { return accHeadId; }
    public void setAccHeadId(String accHeadId) { this.accHeadId = accHeadId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}