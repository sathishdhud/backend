package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CheckInRequest {
    
    private String reservationNo; // Optional for walk-ins
    
    private String guestName; // Optional - will be fetched from reservation if not provided
    
    @NotBlank(message = "Room ID is required")
    private String roomId;
    
    @NotNull(message = "Arrival date is required")
    private LocalDate arrivalDate;
    
    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;
    
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;
    
    private String emailId;
    
    private BigDecimal rate;
    
    private String remarks;
    
    @NotNull(message = "Walk-in flag is required")
    private String walkIn; // Y/N
    
    // Constructors
    public CheckInRequest() {}
    
    // Getters and Setters
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
    
    public String getWalkIn() { return walkIn; }
    public void setWalkIn(String walkIn) { this.walkIn = walkIn; }
}