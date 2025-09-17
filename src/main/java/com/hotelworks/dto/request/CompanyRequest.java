package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CompanyRequest {
    
    // companyId is now optional - will be auto-generated if not provided
    private String companyId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String address1;
    private String address2;
    private String address3;
    private String gstNumber;
    
    // Constructors
    public CompanyRequest() {}
    
    public CompanyRequest(String companyId, String companyName) {
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