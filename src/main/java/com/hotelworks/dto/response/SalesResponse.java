package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesResponse {
    
    private String salesId;
    private String receiptNumber;
    private LocalDate date;
    private String modeOfPayment;
    private BigDecimal amount;
    private String voucherNumber;
    private String narration;
    
    // Constructors
    public SalesResponse() {}
    
    public SalesResponse(String salesId, String receiptNumber, LocalDate date, String modeOfPayment, 
                        BigDecimal amount, String voucherNumber, String narration) {
        this.salesId = salesId;
        this.receiptNumber = receiptNumber;
        this.date = date;
        this.modeOfPayment = modeOfPayment;
        this.amount = amount;
        this.voucherNumber = voucherNumber;
        this.narration = narration;
    }
    
    // Getters and Setters
    public String getSalesId() { return salesId; }
    public void setSalesId(String salesId) { this.salesId = salesId; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getVoucherNumber() { return voucherNumber; }
    public void setVoucherNumber(String voucherNumber) { this.voucherNumber = voucherNumber; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}