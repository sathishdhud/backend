package com.hotelworks.dto.response;

import java.util.List;

public class BillWithTransactionsResponse {
    
    private BillResponse bill;
    private List<PostTransactionResponse> transactions;
    
    // Constructors
    public BillWithTransactionsResponse() {}
    
    public BillWithTransactionsResponse(BillResponse bill, List<PostTransactionResponse> transactions) {
        this.bill = bill;
        this.transactions = transactions;
    }
    
    // Getters and Setters
    public BillResponse getBill() { return bill; }
    public void setBill(BillResponse bill) { this.bill = bill; }
    
    public List<PostTransactionResponse> getTransactions() { return transactions; }
    public void setTransactions(List<PostTransactionResponse> transactions) { this.transactions = transactions; }
}