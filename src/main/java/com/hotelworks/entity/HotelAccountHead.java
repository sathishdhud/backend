package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "hotel_account_head")
public class HotelAccountHead {
    
    @Id
    @Column(name = "acc_head_id")
    private String accHeadId;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "cheque_number")
    private String chequeNumber;
    
    @Column(name = "date")
    private LocalDate date;
    
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
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}