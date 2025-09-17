package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdvanceResponse {
    
    private String receiptNo;
    private String folioNo;
    private String reservationNo;
    private String billNo;
    private String guestName;
    private LocalDate date;
    private LocalDate arrivalDate;
    private LocalDate auditDate;
    private LocalDate shiftDate;
    private String shiftNo;
    private String modeOfPaymentId;
    private String modeOfPaymentName;
    private BigDecimal amount;
    private String creditCardCompany;
    private String cardNumber;
    private String onlineCompanyName;
    private String details;
    private String narration;
    
    // Constructors
    public AdvanceResponse() {}
    
    // Getters and Setters
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
    
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public String getModeOfPaymentName() { return modeOfPaymentName; }
    public void setModeOfPaymentName(String modeOfPaymentName) { this.modeOfPaymentName = modeOfPaymentName; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCreditCardCompany() { return creditCardCompany; }
    public void setCreditCardCompany(String creditCardCompany) { this.creditCardCompany = creditCardCompany; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getOnlineCompanyName() { return onlineCompanyName; }
    public void setOnlineCompanyName(String onlineCompanyName) { this.onlineCompanyName = onlineCompanyName; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}