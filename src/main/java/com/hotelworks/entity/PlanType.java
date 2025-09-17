package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "plan_type")
public class PlanType {
    
    @Id
    @Column(name = "plan_id")
    private String planId;
    
    @NotBlank
    @Column(name = "plan_name", nullable = false)
    private String planName;
    
    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;
    
    // Constructors
    public PlanType() {}
    
    public PlanType(String planId, String planName) {
        this.planId = planId;
        this.planName = planName;
    }
    
    public PlanType(String planId, String planName, BigDecimal discountPercentage) {
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