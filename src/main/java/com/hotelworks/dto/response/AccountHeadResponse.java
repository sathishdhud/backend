package com.hotelworks.dto.response;

import java.time.LocalDate;

public class AccountHeadResponse {
    
    private String accHeadId;
    private String name;
    private String companyName;
    private String chequeNumber;
    private LocalDate date;
    
    // Constructors
    public AccountHeadResponse() {}
    
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