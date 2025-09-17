package com.hotelworks.dto.response;

import java.math.BigDecimal;

public class TaxationResponse {
    
    private String taxId;
    private String taxName;
    private BigDecimal percentage;
    
    // Constructors
    public TaxationResponse() {}
    
    // Getters and Setters
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}