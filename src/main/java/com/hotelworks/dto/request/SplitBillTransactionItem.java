package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a transaction item for split bill functionality
 */
public class SplitBillTransactionItem {
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    private boolean selectedForNewBill;
    
    // For display purposes in split bill UI
    private String voucherNo;
    private String date;
    private String transactionName; // Account head name
    private String amount;
    
    // Constructors
    public SplitBillTransactionItem() {}
    
    public SplitBillTransactionItem(String transactionId, boolean selectedForNewBill) {
        this.transactionId = transactionId;
        this.selectedForNewBill = selectedForNewBill;
    }
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public boolean isSelectedForNewBill() { return selectedForNewBill; }
    public void setSelectedForNewBill(boolean selectedForNewBill) { this.selectedForNewBill = selectedForNewBill; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTransactionName() { return transactionName; }
    public void setTransactionName(String transactionName) { this.transactionName = transactionName; }
    
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}