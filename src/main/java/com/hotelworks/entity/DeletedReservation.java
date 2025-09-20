package com.hotelworks.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserv_deleted")
public class DeletedReservation {
    
    @Id
    @Column(name = "reservation_no")
    private String reservationNo;
    
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @Column(name = "company_id")
    private String companyId;
    
    @Column(name = "plan_id")
    private String planId;
    
    @Column(name = "room_type_id")
    private String roomTypeId;
    
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;
    
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    
    @Column(name = "no_of_days", nullable = false)
    private Integer noOfDays;
    
    @Column(name = "no_of_persons", nullable = false)
    private Integer noOfPersons;
    
    @Column(name = "no_of_rooms", nullable = false)
    private Integer noOfRooms;
    
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;
    
    @Column(name = "email_id")
    private String emailId;
    
    @Column(name = "rate")
    private BigDecimal rate;
    
    @Column(name = "including_gst")
    private String includingGst;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "rooms_checked_in")
    private Integer roomsCheckedIn = 0;
    
    // ID Proof fields
    @Column(name = "id_proof1")
    private String idProof1;
    
    @Column(name = "id_proof2")
    private String idProof2;
    
    @Column(name = "id_proof3")
    private String idProof3;
    
    // Additional fields
    @Column(name = "settlement_type_id")
    private String settlementTypeId;
    
    @Column(name = "arrival_mode_id")
    private String arrivalModeId;
    
    @Column(name = "arrival_details")
    private String arrivalDetails;
    
    @Column(name = "nationality_id")
    private String nationalityId;
    
    @Column(name = "ref_mode_id")
    private String refModeId;
    
    @Column(name = "resv_source_id")
    private String resvSourceId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Constructors
    public DeletedReservation() {}
    
    public DeletedReservation(Reservation reservation) {
        this.reservationNo = reservation.getReservationNo();
        this.guestName = reservation.getGuestName();
        this.companyId = reservation.getCompanyId();
        this.planId = reservation.getPlanId();
        this.roomTypeId = reservation.getRoomTypeId();
        this.arrivalDate = reservation.getArrivalDate();
        this.departureDate = reservation.getDepartureDate();
        this.noOfDays = reservation.getNoOfDays();
        this.noOfPersons = reservation.getNoOfPersons();
        this.noOfRooms = reservation.getNoOfRooms();
        this.mobileNumber = reservation.getMobileNumber();
        this.emailId = reservation.getEmailId();
        this.rate = reservation.getRate();
        this.includingGst = reservation.getIncludingGst();
        this.remarks = reservation.getRemarks();
        this.roomsCheckedIn = reservation.getRoomsCheckedIn();
        this.idProof1 = reservation.getIdProof1();
        this.idProof2 = reservation.getIdProof2();
        this.idProof3 = reservation.getIdProof3();
        this.settlementTypeId = reservation.getSettlementTypeId();
        this.arrivalModeId = reservation.getArrivalModeId();
        this.arrivalDetails = reservation.getArrivalDetails();
        this.nationalityId = reservation.getNationalityId();
        this.refModeId = reservation.getRefModeId();
        this.resvSourceId = reservation.getResvSourceId();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
        this.deletedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    
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
    
    public Integer getRoomsCheckedIn() { return roomsCheckedIn; }
    public void setRoomsCheckedIn(Integer roomsCheckedIn) { this.roomsCheckedIn = roomsCheckedIn; }
    
    // ID Proof getters and setters
    public String getIdProof1() { return idProof1; }
    public void setIdProof1(String idProof1) { this.idProof1 = idProof1; }
    
    public String getIdProof2() { return idProof2; }
    public void setIdProof2(String idProof2) { this.idProof2 = idProof2; }
    
    public String getIdProof3() { return idProof3; }
    public void setIdProof3(String idProof3) { this.idProof3 = idProof3; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}