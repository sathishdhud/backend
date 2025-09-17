package com.hotelworks.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO for split bill request
 */
public class SplitBillRequest {
    
    // Original bill number is set from path parameter, not required in request body
    private String originalBillNo;
    
    @NotEmpty(message = "At least one transaction must be provided")
    @Valid
    private List<SplitBillTransactionItem> selectedTransactions;
    
    private String newBillNarration;
    
    // Constructors
    public SplitBillRequest() {}
    
    public SplitBillRequest(String originalBillNo, List<SplitBillTransactionItem> selectedTransactions) {
        this.originalBillNo = originalBillNo;
        this.selectedTransactions = selectedTransactions;
    }
    
    // Getters and Setters
    public String getOriginalBillNo() { return originalBillNo; }
    public void setOriginalBillNo(String originalBillNo) { this.originalBillNo = originalBillNo; }
    
    public List<SplitBillTransactionItem> getSelectedTransactions() { return selectedTransactions; }
    public void setSelectedTransactions(List<SplitBillTransactionItem> selectedTransactions) { 
        this.selectedTransactions = selectedTransactions; 
    }
    
    public String getNewBillNarration() { return newBillNarration; }
    public void setNewBillNarration(String newBillNarration) { this.newBillNarration = newBillNarration; }
}