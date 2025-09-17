package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ShiftChangeRequest {
    
    @NotNull(message = "Shift date is required")
    private LocalDate shiftDate;
    
    @NotNull(message = "Shift number is required")
    private String shiftNo;
    
    private BigDecimal balance;
    
    // Constructors
    public ShiftChangeRequest() {}
    
    // Getters and Setters
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}