package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "advances")
public class Advance {
    
    @Id
    @Column(name = "receipt_no")
    private String receiptNo;
    
    @Column(name = "folio_no")
    private String folioNo;
    
    @Column(name = "reservation_no")
    private String reservationNo;
    
    @Column(name = "bill_no")
    private String billNo;
    
    @NotBlank
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "arrival_date")
    private LocalDate arrivalDate;
    
    @Column(name = "audit_date")
    private LocalDate auditDate;
    
    @Column(name = "shift_date")
    private LocalDate shiftDate;
    
    @Column(name = "shift_no")
    private String shiftNo;
    
    @NotBlank
    @Column(name = "mode_of_payment_id", nullable = false)
    private String modeOfPaymentId;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "credit_card_company")
    private String creditCardCompany;
    
    @Column(name = "card_number")
    private String cardNumber;
    
    @Column(name = "online_company_name")
    private String onlineCompanyName;
    
    @Column(name = "details")
    private String details;
    
    @Column(name = "narration")
    private String narration;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_no", insertable = false, updatable = false)
    private CheckIn checkIn;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_no", insertable = false, updatable = false)
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_no", insertable = false, updatable = false)
    private FoBill foBill;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mode_of_payment_id", insertable = false, updatable = false)
    private BillSettlementType billSettlementType;
    
    // Constructors
    public Advance() {}
    
    // Getters and Setters
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
    
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public String getModeOfPaymentId() { return modeOfPaymentId; }
    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCreditCardCompany() { return creditCardCompany; }
    public void setCreditCardCompany(String creditCardCompany) { this.creditCardCompany = creditCardCompany; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getOnlineCompanyName() { return onlineCompanyName; }
    public void setOnlineCompanyName(String onlineCompanyName) { this.onlineCompanyName = onlineCompanyName; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public CheckIn getCheckIn() { return checkIn; }
    public void setCheckIn(CheckIn checkIn) { this.checkIn = checkIn; }
    
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    
    public FoBill getFoBill() { return foBill; }
    public void setFoBill(FoBill foBill) { this.foBill = foBill; }
    
    public BillSettlementType getBillSettlementType() { return billSettlementType; }
    public void setBillSettlementType(BillSettlementType billSettlementType) { this.billSettlementType = billSettlementType; }
}