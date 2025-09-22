package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_receipts")
public class SalesReceipt {
    
    @Id
    @Column(name = "receipt_no")
    private String receiptNo;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotBlank
    @Column(name = "mode_of_payment_id", nullable = false)
    private String modeOfPaymentId;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "voucher_no")
    private String voucherNo;
    
    @Column(name = "narration")
    private String narration;
    
    @Column(name = "shift_no")
    private String shiftNo;
    
    @Column(name = "shift_date")
    private LocalDate shiftDate;
    
    // Constructors
    public SalesReceipt() {}
    
    // Getters and Setters
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
}