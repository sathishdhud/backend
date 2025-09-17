package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import java.math.BigDecimal;

public class PlanTypeRequest {
    
    // planId is now optional - will be auto-generated if not provided
    private String planId;
    
    @NotBlank(message = "Plan name is required")
    private String planName;
    
    @DecimalMin(value = "0.0", message = "Discount percentage must be 0 or greater")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    private BigDecimal discountPercentage;
    
    // Constructors
    public PlanTypeRequest() {}
    
    public PlanTypeRequest(String planId, String planName, BigDecimal discountPercentage) {
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