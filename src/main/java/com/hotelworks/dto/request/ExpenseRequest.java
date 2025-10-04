package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {
    
    @NotBlank(message = "Voucher number is required")
    private String voucherNo;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotBlank(message = "Account head is required")
    private String accountHeadId;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private String narration;
    
    @NotBlank(message = "Shift number is required")
    private String shiftNo;
    
    @NotNull(message = "Shift date is required")
    private LocalDate shiftDate;
    
    // New fields for room and bill association
    private String roomNo;
    private String billNo;
    private String folioNo;
    private String guestName;
    
    // Constructors
    public ExpenseRequest() {}
    
    // Getters and Setters
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getAccountHeadId() { return accountHeadId; }
    public void setAccountHeadId(String accountHeadId) { this.accountHeadId = accountHeadId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    
    // New getters and setters
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
}