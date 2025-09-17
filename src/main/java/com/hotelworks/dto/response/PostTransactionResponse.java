package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PostTransactionResponse {
    
    private String transactionId;
    private String folioNo;
    private String billNo;
    private String roomId;
    private String roomNo;
    private String guestName;
    private LocalDate date;
    private LocalDate auditDate;
    private String accHeadId;
    private String accHeadName;
    private String voucherNo;
    private BigDecimal amount;
    private String narration;
    
    // Constructors
    public PostTransactionResponse() {}
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    
    public String getAccHeadId() { return accHeadId; }
    public void setAccHeadId(String accHeadId) { this.accHeadId = accHeadId; }
    
    public String getAccHeadName() { return accHeadName; }
    public void setAccHeadName(String accHeadName) { this.accHeadName = accHeadName; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}