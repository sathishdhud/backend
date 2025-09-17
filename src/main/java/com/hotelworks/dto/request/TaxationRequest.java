package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class TaxationRequest {
    
    private String taxId; // Optional - auto-generated if not provided
    
    private String taxName; // Can be blank as per requirements
    
    private BigDecimal percentage; // Can be zero/blank as per requirements
    
    // Constructors
    public TaxationRequest() {}
    
    public TaxationRequest(String taxId, String taxName, BigDecimal percentage) {
        this.taxId = taxId;
        this.taxName = taxName;
        this.percentage = percentage;
    }
    
    // Getters and Setters
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}