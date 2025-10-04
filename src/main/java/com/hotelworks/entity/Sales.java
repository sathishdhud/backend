package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sales {
    
    @Id
    @Column(name = "sales_id")
    private String salesId;
    
    @NotBlank
    @Column(name = "receipt_number", nullable = false)
    private String receiptNumber;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotBlank
    @Column(name = "mode_of_payment", nullable = false)
    private String modeOfPayment;
    
    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @NotBlank
    @Column(name = "voucher_number", nullable = false)
    private String voucherNumber;
    
    @Column(name = "narration")
    private String narration;
    
    // Constructors
    public Sales() {}
    
    // Getters and Setters
    public String getSalesId() { return salesId; }
    public void setSalesId(String salesId) { this.salesId = salesId; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getVoucherNumber() { return voucherNumber; }
    public void setVoucherNumber(String voucherNumber) { this.voucherNumber = voucherNumber; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}