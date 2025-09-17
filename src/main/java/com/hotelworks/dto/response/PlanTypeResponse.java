package com.hotelworks.dto.response;

import java.math.BigDecimal;

public class PlanTypeResponse {
    
    private String planId;
    private String planName;
    private BigDecimal discountPercentage;
    
    // Constructors
    public PlanTypeResponse() {}
    
    public PlanTypeResponse(String planId, String planName, BigDecimal discountPercentage) {
        this.planId = planId;
        this.planName = planName;
        this.discountPercentage = discountPercentage;
    }
    
    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
}