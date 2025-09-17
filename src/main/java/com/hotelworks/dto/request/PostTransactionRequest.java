package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PostTransactionRequest {
    
    private String folioNo; // For in-house guests
    private String billNo; // For checkout guests
    private String roomId;
    
    @NotBlank(message = "Guest name is required")
    private String guestName;
    
    private LocalDate date; // Manual entry for checkout guests
    
    @NotBlank(message = "Account head is required")
    private String accHeadId;
    
    private String voucherNo;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private String narration;
    
    // Constructors
    public PostTransactionRequest() {}
    
    // Getters and Setters
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getAccHeadId() { return accHeadId; }
    public void setAccHeadId(String accHeadId) { this.accHeadId = accHeadId; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}