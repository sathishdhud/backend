package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for processing bill settlement payments
 */
public class BillSettlementRequest {
    
    // Bill number is set from path parameter, not required in request body
    private String billNo;
    
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal paymentAmount;
    
    @NotBlank(message = "Payment mode is required")
    private String modeOfPaymentId;
    
    // Payment details for different modes
    private String creditCardCompany;
    private String cardNumber;
    private String onlineCompanyName;
    private String details;
    private String paymentNotes;
    
    // Constructors
    public BillSettlementRequest() {}
    
    public BillSettlementRequest(String billNo, BigDecimal paymentAmount, String modeOfPaymentId) {
        this.billNo = billNo;
        this.paymentAmount = paymentAmount;
        this.modeOfPaymentId = modeOfPaymentId;
    }
    
    // Getters and Setters
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public String getCreditCardCompany() { return creditCardCompany; }
    public void setCreditCardCompany(String creditCardCompany) { this.creditCardCompany = creditCardCompany; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getOnlineCompanyName() { return onlineCompanyName; }
    public void setOnlineCompanyName(String onlineCompanyName) { this.onlineCompanyName = onlineCompanyName; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
}