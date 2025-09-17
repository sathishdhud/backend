package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "post_transactions")
public class PostTransaction {
    
    @Id
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "folio_no")
    private String folioNo;
    
    @Column(name = "bill_no")
    private String billNo;
    
    @Column(name = "room_id")
    private String roomId;
    
    @NotBlank
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "audit_date")
    private LocalDate auditDate;
    
    @NotBlank
    @Column(name = "acc_head_id", nullable = false)
    private String accHeadId;
    
    @Column(name = "voucher_no")
    private String voucherNo;
    
    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "narration")
    private String narration;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_no", insertable = false, updatable = false)
    private CheckIn checkIn;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_no", insertable = false, updatable = false)
    private FoBill foBill;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_head_id", insertable = false, updatable = false)
    private HotelAccountHead hotelAccountHead;
    
    // Constructors
    public PostTransaction() {}
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    
    public String getAccHeadId() { return accHeadId; }
    public void setAccHeadId(String accHeadId) { this.accHeadId = accHeadId; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public CheckIn getCheckIn() { return checkIn; }
    public void setCheckIn(CheckIn checkIn) { this.checkIn = checkIn; }
    
    public FoBill getFoBill() { return foBill; }
    public void setFoBill(FoBill foBill) { this.foBill = foBill; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    public HotelAccountHead getHotelAccountHead() { return hotelAccountHead; }
    public void setHotelAccountHead(HotelAccountHead hotelAccountHead) { this.hotelAccountHead = hotelAccountHead; }
}