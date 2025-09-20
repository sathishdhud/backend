package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ShiftCloseRequest {
    
    @NotNull(message = "Balance is required")
    private BigDecimal balance;
    
    // Constructors
    public ShiftCloseRequest() {}
    
    // Getters and Setters
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}