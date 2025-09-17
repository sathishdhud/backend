package com.hotelworks.dto.response;

public class AccountHeadResponse {
    
    private String accountHeadId;
    private String accountName;
    
    // Constructors
    public AccountHeadResponse() {}
    
    // Getters and Setters
    public String getAccountHeadId() { return accountHeadId; }
    public void setAccountHeadId(String accountHeadId) { this.accountHeadId = accountHeadId; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
}