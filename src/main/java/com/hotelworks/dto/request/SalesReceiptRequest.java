package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesReceiptRequest {
    
    @NotBlank(message = "Receipt number is required")
    private String receiptNo;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotBlank(message = "Mode of payment is required")
    private String modeOfPaymentId;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private String voucherNo;
    
    private String narration;
    
    @NotBlank(message = "Shift number is required")
    private String shiftNo;
    
    @NotNull(message = "Shift date is required")
    private LocalDate shiftDate;
    
    // Constructors
    public SalesReceiptRequest() {}
    
    // Getters and Setters
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
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