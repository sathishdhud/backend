package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "refunds")
public class Refund {
    
    @Id
    @Column(name = "refund_id")
    private String refundId;
    
    @NotBlank
    @Column(name = "receipt_no", nullable = false)
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
    
    @Column(name = "bill_no")
    private String billNo;
    
    @Column(name = "folio_no")
    private String folioNo;
    
    @Column(name = "guest_name")
    private String guestName;
    
    // Constructors
    public Refund() {}
    
    // Getters and Setters
    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }
    
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
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
}