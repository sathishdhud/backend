package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for bill settlement response
 */
public class BillSettlementResponse {
    
    private String billNo;
    private String guestName;
    private String folioNo;
    private String roomNo;
    private BigDecimal originalAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal balanceAmount;
    private String settlementStatus; // PENDING, PARTIAL, SETTLED
    private LocalDateTime lastPaymentDate;
    private LocalDateTime settlementDate;
    private BigDecimal currentPaymentAmount;
    private String modeOfPaymentId;
    private String modeOfPaymentName;
    private String paymentNotes;
    
    // Constructors
    public BillSettlementResponse() {}
    
    // Getters and Setters
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    
    public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
    public void setTotalPaidAmount(BigDecimal totalPaidAmount) { this.totalPaidAmount = totalPaidAmount; }
    
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
    
    public LocalDateTime getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    
    public LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }
    
    public BigDecimal getCurrentPaymentAmount() { return currentPaymentAmount; }
    public void setCurrentPaymentAmount(BigDecimal currentPaymentAmount) { this.currentPaymentAmount = currentPaymentAmount; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public String getModeOfPaymentName() { return modeOfPaymentName; }
    public void setModeOfPaymentName(String modeOfPaymentName) { this.modeOfPaymentName = modeOfPaymentName; }
    
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
}