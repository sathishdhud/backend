package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fobill")
public class FoBill {
    
    @Id
    @Column(name = "bill_no")
    private String billNo;
    
    @NotBlank
    @Column(name = "folio_no", nullable = false)
    private String folioNo;
    
    @NotBlank
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @NotBlank
    @Column(name = "room_id", nullable = false)
    private String roomId;
    
    @NotNull
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "advance_amount")
    private BigDecimal advanceAmount;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    
    // Split bill tracking fields
    @Column(name = "original_bill_no")
    private String originalBillNo; // Reference to original bill if this is a split
    
    @Column(name = "is_split_bill")
    private Boolean isSplitBill = false; // Flag to indicate if this is a split bill
    
    @Column(name = "split_sequence")
    private Integer splitSequence; // Sequential number for splits (1, 2, 3, etc.)
    
    // Bill settlement tracking fields
    @Column(name = "settlement_status")
    private String settlementStatus = "PENDING"; // PENDING, PARTIAL, SETTLED
    
    @Column(name = "paid_amount")
    private BigDecimal paidAmount = BigDecimal.ZERO; // Amount paid so far
    
    @Column(name = "balance_amount")
    private BigDecimal balanceAmount; // Remaining balance to be paid
    
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate; // Date when bill was fully settled
    
    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate; // Date of last payment
    
    @Column(name = "payment_notes")
    private String paymentNotes; // Additional payment notes
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_no", insertable = false, updatable = false)
    private CheckIn checkIn;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public FoBill() {}
    
    // Getters and Setters
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount;
        calculateBalanceAmount();
    }
    
    public BigDecimal getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public CheckIn getCheckIn() { return checkIn; }
    public void setCheckIn(CheckIn checkIn) { this.checkIn = checkIn; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    // Split bill getters and setters
    public String getOriginalBillNo() { return originalBillNo; }
    public void setOriginalBillNo(String originalBillNo) { this.originalBillNo = originalBillNo; }
    
    public Boolean getIsSplitBill() { return isSplitBill; }
    public void setIsSplitBill(Boolean isSplitBill) { this.isSplitBill = isSplitBill; }
    
    public Integer getSplitSequence() { return splitSequence; }
    public void setSplitSequence(Integer splitSequence) { this.splitSequence = splitSequence; }
    
    // Settlement tracking getters and setters
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
    
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { 
        this.paidAmount = paidAmount;
        calculateBalanceAmount();
    }
    
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { 
        this.balanceAmount = balanceAmount;
        // Also update settlement status when balance is directly set
        updateSettlementStatus();
    }
    
    public LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }
    
    public LocalDateTime getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
    
    // Utility methods
    public void calculateBalanceAmount() {
        // New calculation: Balance = Rate from reservation - Advances
        if (this.checkIn != null && this.checkIn.getReservation() != null) {
            // Get rate from reservation
            BigDecimal rate = this.checkIn.getReservation().getRate() != null ? 
                this.checkIn.getReservation().getRate() : BigDecimal.ZERO;
            // Get advances
            BigDecimal advances = this.advanceAmount != null ? this.advanceAmount : BigDecimal.ZERO;
            // Calculate balance
            BigDecimal calculatedBalance = rate.subtract(advances);
            // Ensure balance never goes below zero
            this.balanceAmount = calculatedBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : calculatedBalance;
        } else {
            // Fallback to original calculation if reservation data is not available
            BigDecimal total = totalAmount != null ? totalAmount : BigDecimal.ZERO;
            BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
            BigDecimal calculatedBalance = total.subtract(paid);
            // Ensure balance never goes below zero
            this.balanceAmount = calculatedBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : calculatedBalance;
        }
    }
    
    public void updateSettlementStatus() {
        if (balanceAmount == null) {
            calculateBalanceAmount();
        }
        
        if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.settlementStatus = "SETTLED";
            if (this.settlementDate == null) {
                this.settlementDate = LocalDateTime.now();
            }
        } else if (paidAmount != null && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.settlementStatus = "PARTIAL";
        } else {
            this.settlementStatus = "PENDING";
        }
    }
}