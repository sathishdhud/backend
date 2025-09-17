package com.hotelworks.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {
    
    private String reservationNo;
    private String guestName;
    private String companyId;
    private String companyName;
    private String planId;
    private String planName;
    private String roomTypeId;
    private String roomTypeName;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Integer noOfDays;
    private Integer noOfPersons;
    private Integer noOfRooms;
    private String mobileNumber;
    private String emailId;
    private BigDecimal rate;
    private String includingGst;
    private String remarks;
    private Integer roomsCheckedIn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields as per your request
    private String settlementTypeId;
    private String settlementTypeName;
    private String arrivalModeId;
    private String arrivalModeName;
    private String arrivalDetails;
    private String nationalityId;
    private String nationalityName;
    private String refModeId;
    private String refModeName;
    private String resvSourceId;
    private String resvSourceName;
    
    // Constructors
    public ReservationResponse() {}
    
    // Getters and Setters
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    
    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
    
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    
    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
    
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    
    public Integer getNoOfDays() { return noOfDays; }
    public void setNoOfDays(Integer noOfDays) { this.noOfDays = noOfDays; }
    
    public Integer getNoOfPersons() { return noOfPersons; }
    public void setNoOfPersons(Integer noOfPersons) { this.noOfPersons = noOfPersons; }
    
    public Integer getNoOfRooms() { return noOfRooms; }
    public void setNoOfRooms(Integer noOfRooms) { this.noOfRooms = noOfRooms; }
    
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    
    public String getIncludingGst() { return includingGst; }
    public void setIncludingGst(String includingGst) { this.includingGst = includingGst; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public Integer getRoomsCheckedIn() { return roomsCheckedIn; }
    public void setRoomsCheckedIn(Integer roomsCheckedIn) { this.roomsCheckedIn = roomsCheckedIn; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Additional getters and setters
    public String getSettlementTypeId() { return settlementTypeId; }
    public void setSettlementTypeId(String settlementTypeId) { this.settlementTypeId = settlementTypeId; }
    
    public String getSettlementTypeName() { return settlementTypeName; }
    public void setSettlementTypeName(String settlementTypeName) { this.settlementTypeName = settlementTypeName; }
    
    public String getArrivalModeId() { return arrivalModeId; }
    public void setArrivalModeId(String arrivalModeId) { this.arrivalModeId = arrivalModeId; }
    
    public String getArrivalModeName() { return arrivalModeName; }
    public void setArrivalModeName(String arrivalModeName) { this.arrivalModeName = arrivalModeName; }
    
    public String getArrivalDetails() { return arrivalDetails; }
    public void setArrivalDetails(String arrivalDetails) { this.arrivalDetails = arrivalDetails; }
    
    public String getNationalityId() { return nationalityId; }
    public void setNationalityId(String nationalityId) { this.nationalityId = nationalityId; }
    
    public String getNationalityName() { return nationalityName; }
    public void setNationalityName(String nationalityName) { this.nationalityName = nationalityName; }
    
    public String getRefModeId() { return refModeId; }
    public void setRefModeId(String refModeId) { this.refModeId = refModeId; }
    
    public String getRefModeName() { return refModeName; }
    public void setRefModeName(String refModeName) { this.refModeName = refModeName; }
    
    public String getResvSourceId() { return resvSourceId; }
    public void setResvSourceId(String resvSourceId) { this.resvSourceId = resvSourceId; }
    
    public String getResvSourceName() { return resvSourceName; }
    public void setResvSourceName(String resvSourceName) { this.resvSourceName = resvSourceName; }
}