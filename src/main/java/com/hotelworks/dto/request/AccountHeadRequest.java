package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class AccountHeadRequest {
    
    @NotBlank(message = "Account head ID is required")
    private String accHeadId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String companyName;
    
    private String chequeNumber;
    
    private LocalDate date;
    
    // Constructors
    public AccountHeadRequest() {}
    
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