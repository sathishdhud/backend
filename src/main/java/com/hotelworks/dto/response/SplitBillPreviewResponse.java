package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for split bill preview showing original bill transactions with selection options
 */
public class SplitBillPreviewResponse {
    
    private String originalBillNo;
    private String guestName;
    private String folioNo;
    private String roomNo;
    private BigDecimal originalTotalAmount;
    private BigDecimal originalAdvanceAmount;
    private List<SplitBillTransactionResponse> transactions;
    private List<AdvanceResponse> advances;
    
    // Constructors
    public SplitBillPreviewResponse() {}
    
    // Getters and Setters
    public String getOriginalBillNo() { return originalBillNo; }
    public void setOriginalBillNo(String originalBillNo) { this.originalBillNo = originalBillNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public BigDecimal getOriginalTotalAmount() { return originalTotalAmount; }
    public void setOriginalTotalAmount(BigDecimal originalTotalAmount) { this.originalTotalAmount = originalTotalAmount; }
    
    public BigDecimal getOriginalAdvanceAmount() { return originalAdvanceAmount; }
    public void setOriginalAdvanceAmount(BigDecimal originalAdvanceAmount) { this.originalAdvanceAmount = originalAdvanceAmount; }
    
    public List<SplitBillTransactionResponse> getTransactions() { return transactions; }
    public void setTransactions(List<SplitBillTransactionResponse> transactions) { this.transactions = transactions; }
    
    public List<AdvanceResponse> getAdvances() { return advances; }
    public void setAdvances(List<AdvanceResponse> advances) { this.advances = advances; }
}