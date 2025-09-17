package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationRequest {
    
    @NotBlank(message = "Guest name is required")
    private String guestName;
    
    private String companyId;
    
    private String planId;
    
    private String roomTypeId;
    
    @NotNull(message = "Arrival date is required")
    private LocalDate arrivalDate;
    
    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;
    
    @NotNull(message = "Number of days is required")
    private Integer noOfDays;
    
    @NotNull(message = "Number of persons is required")
    private Integer noOfPersons;
    
    @NotNull(message = "Number of rooms is required")
    private Integer noOfRooms;
    
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;
    
    private String emailId;
    
    private BigDecimal rate;
    
    private String includingGst = "N"; // Y/N, default N
    
    private String remarks;
    
    // Additional fields as per your request
    private String settlementTypeId;
    private String arrivalModeId;
    private String arrivalDetails;
    private String nationalityId;
    private String refModeId;
    private String resvSourceId;
    
    // Constructors
    public ReservationRequest() {}
    
    // Getters and Setters
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
    
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
    
    // Additional getters and setters
    public String getSettlementTypeId() { return settlementTypeId; }
    public void setSettlementTypeId(String settlementTypeId) { this.settlementTypeId = settlementTypeId; }
    
    public String getArrivalModeId() { return arrivalModeId; }
    public void setArrivalModeId(String arrivalModeId) { this.arrivalModeId = arrivalModeId; }
    
    public String getArrivalDetails() { return arrivalDetails; }
    public void setArrivalDetails(String arrivalDetails) { this.arrivalDetails = arrivalDetails; }
    
    public String getNationalityId() { return nationalityId; }
    public void setNationalityId(String nationalityId) { this.nationalityId = nationalityId; }
    
    public String getRefModeId() { return refModeId; }
    public void setRefModeId(String refModeId) { this.refModeId = refModeId; }
    
    public String getResvSourceId() { return resvSourceId; }
    public void setResvSourceId(String resvSourceId) { this.resvSourceId = resvSourceId; }
}