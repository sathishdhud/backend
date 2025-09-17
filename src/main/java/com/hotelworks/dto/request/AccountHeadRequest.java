package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AccountHeadRequest {
    
    private String accountHeadId; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Account name cannot be blank")
    private String accountName;
    
    // Constructors
    public AccountHeadRequest() {}
    
    public AccountHeadRequest(String accountHeadId, String accountName) {
        this.accountHeadId = accountHeadId;
        this.accountName = accountName;
    }
    
    // Getters and Setters
    public String getAccountHeadId() { return accountHeadId; }
    public void setAccountHeadId(String accountHeadId) { this.accountHeadId = accountHeadId; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
}