package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AdvanceSummaryResponse {
    
    private String referenceNumber; // Reservation No, Folio No, or Bill No
    private String guestName;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String roomNo;
    private BigDecimal totalAmount;
    private int totalCount;
    private List<AdvanceDetail> advanceDetails;
    
    // Constructors
    public AdvanceSummaryResponse() {}
    
    // Getters and Setters
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
    
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    
    public List<AdvanceDetail> getAdvanceDetails() { return advanceDetails; }
    public void setAdvanceDetails(List<AdvanceDetail> advanceDetails) { this.advanceDetails = advanceDetails; }
    
    // Inner class for advance details
    public static class AdvanceDetail {
        private String receiptNo;
        private LocalDate date;
        private String modeOfPaymentName;
        private BigDecimal amount;
        private String narration;
        
        // Constructors
        public AdvanceDetail() {}
        
        // Getters and Setters
        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public String getModeOfPaymentName() { return modeOfPaymentName; }
        public void setModeOfPaymentName(String modeOfPaymentName) { this.modeOfPaymentName = modeOfPaymentName; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getNarration() { return narration; }
        public void setNarration(String narration) { this.narration = narration; }
    }
}