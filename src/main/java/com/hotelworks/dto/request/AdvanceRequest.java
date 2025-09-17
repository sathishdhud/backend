package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AdvanceRequest {

    
    
    private String folioNo; // For in-house guests
    private String reservationNo; // For reservation advances
    private String billNo; // For checkout guest advances
    
    @NotBlank(message = "Guest name is required")
    private String guestName;
    
    private LocalDate date; // Optional, defaults to current date
    
    private LocalDate arrivalDate;
    
    @NotBlank(message = "Mode of payment is required")
    private String modeOfPaymentId;
    
    private BigDecimal amount;
    
    private String creditCardCompany;
    
    private String cardNumber;
    
    private String onlineCompanyName;
    
    private String details;
    
    private String narration;
    
    // Constructors
    public AdvanceRequest() {}
    
    // Getters and Setters
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
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
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