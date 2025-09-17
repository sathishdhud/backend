package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "checkin")
public class CheckIn {
    
    @Id
    @Column(name = "folio_no")
    private String folioNo;
    
    @Column(name = "reservation_no")
    private String reservationNo;
    
    @NotBlank
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @NotBlank
    @Column(name = "room_id", nullable = false)
    private String roomId;
    
    @NotNull
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;
    
    @NotNull
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    
    @NotBlank
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;
    
    @Column(name = "email_id")
    private String emailId;
    
    @Column(name = "rate")
    private BigDecimal rate;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "audit_date")
    private LocalDate auditDate;
    
    @Column(name = "walk_in")
    private String walkIn; // Y/N
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_no", insertable = false, updatable = false)
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
    
    // Constructors
    public CheckIn() {}
    
    // Getters and Setters
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
    
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    
    public String getWalkIn() { return walkIn; }
    public void setWalkIn(String walkIn) { this.walkIn = walkIn; }
    
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}