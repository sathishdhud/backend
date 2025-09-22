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
    
    // ID Proof fields (newly added)
    private String idProof1;
    private String idProof2;
    private String idProof3;
    
    // Additional fields as per your request (newly added)
    private String companyId;
    private String planId;
    private String roomTypeId;
    private String settlementTypeId;
    private String arrivalModeId;
    private String arrivalDetails;
    private String nationalityId;
    private String refModeId;
    private String resvSourceId;
    
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
    
    // ID Proof getters and setters (newly added)
    public String getIdProof1() { return idProof1; }
    public void setIdProof1(String idProof1) { this.idProof1 = idProof1; }
    
    public String getIdProof2() { return idProof2; }
    public void setIdProof2(String idProof2) { this.idProof2 = idProof2; }
    
    public String getIdProof3() { return idProof3; }
    public void setIdProof3(String idProof3) { this.idProof3 = idProof3; }
    
    // Additional getters and setters (newly added)
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
    
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