package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponse {
    
    private String transactionId;
    private String voucherNo;
    private LocalDate date;
    private String accountHeadId;
    private String accountHeadName;
    private BigDecimal amount;
    private String narration;
    private String shiftNo;
    private LocalDate shiftDate;
    
    // Constructors
    public ExpenseResponse() {}
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getAccountHeadId() { return accountHeadId; }
    public void setAccountHeadId(String accountHeadId) { this.accountHeadId = accountHeadId; }
    
    public String getAccountHeadName() { return accountHeadName; }
    public void setAccountHeadName(String accountHeadName) { this.accountHeadName = accountHeadName; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
}