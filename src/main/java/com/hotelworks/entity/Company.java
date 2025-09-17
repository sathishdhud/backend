package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "company")
public class Company {
    
    @Id
    @Column(name = "company_id")
    private String companyId;
    
    @NotBlank
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "address1")
    private String address1;
    
    @Column(name = "address2")
    private String address2;
    
    @Column(name = "address3")
    private String address3;
    
    @Column(name = "gst_number")
    private String gstNumber;
    
    // Constructors
    public Company() {}
    
    public Company(String companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
    }
    
    // Getters and Setters
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    
    public String getAddress3() { return address3; }
    public void setAddress3(String address3) { this.address3 = address3; }
    
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
}