package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesReceiptResponse {
    
    private String receiptNo;
    private LocalDate date;
    private String modeOfPaymentId;
    private String modeOfPaymentName;
    private BigDecimal amount;
    private String voucherNo;
    private String narration;
    private String shiftNo;
    private LocalDate shiftDate;
    
    // Constructors
    public SalesReceiptResponse() {}
    
    // Getters and Setters
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public String getModeOfPaymentName() { return modeOfPaymentName; }
    public void setModeOfPaymentName(String modeOfPaymentName) { this.modeOfPaymentName = modeOfPaymentName; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
}