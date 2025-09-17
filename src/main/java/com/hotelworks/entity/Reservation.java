package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @Column(name = "reservation_no")
    private String reservationNo;
    
    @NotBlank
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @Column(name = "company_id")
    private String companyId;
    
    @Column(name = "plan_id")
    private String planId;
    
    @Column(name = "room_type_id")
    private String roomTypeId;
    
    @NotNull
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;
    
    @NotNull
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    
    @NotNull
    @Column(name = "no_of_days", nullable = false)
    private Integer noOfDays;
    
    @NotNull
    @Column(name = "no_of_persons", nullable = false)
    private Integer noOfPersons;
    
    @NotNull
    @Column(name = "no_of_rooms", nullable = false)
    private Integer noOfRooms;
    
    @NotBlank
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;
    
    @Column(name = "email_id")
    private String emailId;
    
    @Column(name = "rate")
    private BigDecimal rate;
    
    @Column(name = "including_gst")
    private String includingGst; // Y/N
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "rooms_checked_in")
    private Integer roomsCheckedIn = 0;
    
    // Additional fields as per your request
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
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private PlanType planType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_type_id", insertable = false, updatable = false)
    private BillSettlementType settlementType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_mode_id", insertable = false, updatable = false)
    private ArrivalMode arrivalMode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id", insertable = false, updatable = false)
    private Nationality nationality;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_mode_id", insertable = false, updatable = false)
    private RefMode refMode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resv_source_id", insertable = false, updatable = false)
    private ResvSource resvSource;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (roomsCheckedIn == null) {
            roomsCheckedIn = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Reservation() {}
    
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
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    
    public BillSettlementType getSettlementType() { return settlementType; }
    public void setSettlementType(BillSettlementType settlementType) { this.settlementType = settlementType; }
    
    public ArrivalMode getArrivalMode() { return arrivalMode; }
    public void setArrivalMode(ArrivalMode arrivalMode) { this.arrivalMode = arrivalMode; }
    
    public Nationality getNationality() { return nationality; }
    public void setNationality(Nationality nationality) { this.nationality = nationality; }
    
    public RefMode getRefMode() { return refMode; }
    public void setRefMode(RefMode refMode) { this.refMode = refMode; }
    
    public ResvSource getResvSource() { return resvSource; }
    public void setResvSource(ResvSource resvSource) { this.resvSource = resvSource; }
}