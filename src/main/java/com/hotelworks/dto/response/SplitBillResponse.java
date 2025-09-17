package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for split bill operation result
 */
public class SplitBillResponse {
    
    private String originalBillNo;
    private String newBillNo;
    private String guestName;
    private String folioNo;
    private String roomNo;
    
    // Original bill after split
    private BigDecimal originalBillRemainingAmount;
    private BigDecimal originalBillAdvanceAmount;
    private BigDecimal originalBillBalanceAmount;
    
    // New bill details
    private BigDecimal newBillTotalAmount;
    private BigDecimal newBillAdvanceAmount;
    private BigDecimal newBillBalanceAmount;
    
    private LocalDateTime splitDateTime;
    private String splitNarration;
    
    // Summary
    private int transactionsMovedToNewBill;
    private int transactionsRemainingInOriginal;
    
    // Constructors
    public SplitBillResponse() {}
    
    // Getters and Setters
    public String getOriginalBillNo() { return originalBillNo; }
    public void setOriginalBillNo(String originalBillNo) { this.originalBillNo = originalBillNo; }
    
    public String getNewBillNo() { return newBillNo; }
    public void setNewBillNo(String newBillNo) { this.newBillNo = newBillNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public BigDecimal getOriginalBillRemainingAmount() { return originalBillRemainingAmount; }
    public void setOriginalBillRemainingAmount(BigDecimal originalBillRemainingAmount) { 
        this.originalBillRemainingAmount = originalBillRemainingAmount; 
    }
    
    public BigDecimal getOriginalBillAdvanceAmount() { return originalBillAdvanceAmount; }
    public void setOriginalBillAdvanceAmount(BigDecimal originalBillAdvanceAmount) { 
        this.originalBillAdvanceAmount = originalBillAdvanceAmount; 
    }
    
    public BigDecimal getOriginalBillBalanceAmount() { return originalBillBalanceAmount; }
    public void setOriginalBillBalanceAmount(BigDecimal originalBillBalanceAmount) { 
        this.originalBillBalanceAmount = originalBillBalanceAmount; 
    }
    
    public BigDecimal getNewBillTotalAmount() { return newBillTotalAmount; }
    public void setNewBillTotalAmount(BigDecimal newBillTotalAmount) { this.newBillTotalAmount = newBillTotalAmount; }
    
    public BigDecimal getNewBillAdvanceAmount() { return newBillAdvanceAmount; }
    public void setNewBillAdvanceAmount(BigDecimal newBillAdvanceAmount) { this.newBillAdvanceAmount = newBillAdvanceAmount; }
    
    public BigDecimal getNewBillBalanceAmount() { return newBillBalanceAmount; }
    public void setNewBillBalanceAmount(BigDecimal newBillBalanceAmount) { this.newBillBalanceAmount = newBillBalanceAmount; }
    
    public LocalDateTime getSplitDateTime() { return splitDateTime; }
    public void setSplitDateTime(LocalDateTime splitDateTime) { this.splitDateTime = splitDateTime; }
    
    public String getSplitNarration() { return splitNarration; }
    public void setSplitNarration(String splitNarration) { this.splitNarration = splitNarration; }
    
    public int getTransactionsMovedToNewBill() { return transactionsMovedToNewBill; }
    public void setTransactionsMovedToNewBill(int transactionsMovedToNewBill) { 
        this.transactionsMovedToNewBill = transactionsMovedToNewBill; 
    }
    
    public int getTransactionsRemainingInOriginal() { return transactionsRemainingInOriginal; }
    public void setTransactionsRemainingInOriginal(int transactionsRemainingInOriginal) { 
        this.transactionsRemainingInOriginal = transactionsRemainingInOriginal; 
    }
}