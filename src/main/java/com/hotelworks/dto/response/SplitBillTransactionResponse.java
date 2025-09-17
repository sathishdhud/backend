package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing transaction details for split bill preview
 */
public class SplitBillTransactionResponse {
    
    private String transactionId;
    private String voucherNo;
    private LocalDate date;
    private String transactionName; // Account head name
    private BigDecimal amount;
    private String narration;
    private boolean selectedForNewBill;
    
    // Constructors
    public SplitBillTransactionResponse() {}
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getTransactionName() { return transactionName; }
    public void setTransactionName(String transactionName) { this.transactionName = transactionName; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public boolean isSelectedForNewBill() { return selectedForNewBill; }
    public void setSelectedForNewBill(boolean selectedForNewBill) { this.selectedForNewBill = selectedForNewBill; }
}