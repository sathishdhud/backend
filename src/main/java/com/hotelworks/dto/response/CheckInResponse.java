package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CheckInResponse {
    
    private String folioNo;
    private String reservationNo;
    private String guestName;
    private String roomId;
    private String roomNo;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String mobileNumber;
    private String emailId;
    private BigDecimal rate;
    private String remarks;
    private LocalDate auditDate;
    private String walkIn;
    private BigDecimal totalAdvances;
    
    // Constructors
    public CheckInResponse() {}
    
    // Getters and Setters
    public String getFolioNo() { return folioNo; }
    public void setFolioNo(String folioNo) { this.folioNo = folioNo; }
    
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
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
    
    public BigDecimal getTotalAdvances() { return totalAdvances; }
    public void setTotalAdvances(BigDecimal totalAdvances) { this.totalAdvances = totalAdvances; }
}