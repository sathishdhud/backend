package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesRequest {
    
    @NotBlank
    private String receiptNumber;
    
    @NotNull
    private LocalDate date;
    
    @NotBlank
    private String modeOfPayment;
    
    @NotNull
    private BigDecimal amount;
    
    @NotBlank
    private String voucherNumber;
    
    private String narration;
    
    // Constructors
    public SalesRequest() {}
    
    // Getters and Setters
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