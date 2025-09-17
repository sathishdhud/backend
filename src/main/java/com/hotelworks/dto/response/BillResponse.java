package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillResponse {
    
    private String billNo;
    private String folioNo;
    private String guestName;
    private String roomId;
    private String roomNo;
    // Total amount from all transactions
    private BigDecimal totalTransactionAmount;
    // Total bill amount (might be different from transaction amount in some cases)
    private BigDecimal totalAmount;
    private BigDecimal advanceAmount;
    private BigDecimal balanceAmount;
    private LocalDateTime generatedAt;
    private List<PostTransactionResponse> transactions;
    private List<AdvanceResponse> advances;
    
    // Split bill tracking fields
    private Boolean isSplitBill;
    private Integer splitSequence;
    private String originalBillNo;
    
    // Bill settlement fields
    private String settlementStatus; // PENDING, PARTIAL, SETTLED
    private BigDecimal paidAmount;
    private LocalDateTime settlementDate;
    private LocalDateTime lastPaymentDate;
    private String paymentNotes;
    
    // Constructors
    public BillResponse() {}
    
    // Getters and Setters
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    /**
     * Get total transaction amount (sum of all transactions for this bill)
     * @return Total transaction amount
     */
    public BigDecimal getTotalTransactionAmount() { return totalTransactionAmount; }
    public void setTotalTransactionAmount(BigDecimal totalTransactionAmount) { this.totalTransactionAmount = totalTransactionAmount; }
    
    /**
     * Get total bill amount
     * @return Total bill amount
     */
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }
    
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public List<PostTransactionResponse> getTransactions() { return transactions; }
    public void setTransactions(List<PostTransactionResponse> transactions) { this.transactions = transactions; }
    
    public List<AdvanceResponse> getAdvances() { return advances; }
    public void setAdvances(List<AdvanceResponse> advances) { this.advances = advances; }
    
    // Split bill getters and setters
    public Boolean getIsSplitBill() { return isSplitBill; }
    public void setIsSplitBill(Boolean isSplitBill) { this.isSplitBill = isSplitBill; }
    
    public Integer getSplitSequence() { return splitSequence; }
    public void setSplitSequence(Integer splitSequence) { this.splitSequence = splitSequence; }
    
    public String getOriginalBillNo() { return originalBillNo; }
    public void setOriginalBillNo(String originalBillNo) { this.originalBillNo = originalBillNo; }
    
    // Settlement getters and setters
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
    
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    
    public LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }
    
    public LocalDateTime getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
}