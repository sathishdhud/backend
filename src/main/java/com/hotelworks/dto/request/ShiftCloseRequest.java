package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ShiftCloseRequest {
    
    @NotNull(message = "Opening balance is required")
    private BigDecimal openingBalance;
    
    @NotNull(message = "Closing balance is required")
    private BigDecimal closingBalance;
    
    @NotNull(message = "Total income is required")
    private BigDecimal totalIncome;
    
    @NotNull(message = "Total expense is required")
    private BigDecimal totalExpense;
    
    // Constructors
    public ShiftCloseRequest() {}
    
    // Getters and Setters
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
    
    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }
    
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    
    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
}