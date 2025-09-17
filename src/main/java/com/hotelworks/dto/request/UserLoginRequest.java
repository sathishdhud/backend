package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {
    
    @NotBlank(message = "User name cannot be blank")
    private String userName;
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    // Constructors
    public UserLoginRequest() {}
    
    public UserLoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}