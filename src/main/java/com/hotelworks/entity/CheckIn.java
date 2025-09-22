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
    
    // ID Proof fields (newly added)
    @Column(name = "id_proof1")
    private String idProof1;
    
    @Column(name = "id_proof2")
    private String idProof2;
    
    @Column(name = "id_proof3")
    private String idProof3;
    
    // Additional fields as per your request (newly added)
    @Column(name = "company_id")
    private String companyId;
    
    @Column(name = "plan_id")
    private String planId;
    
    @Column(name = "room_type_id")
    private String roomTypeId;
    
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
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_no", insertable = false, updatable = false)
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
    
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
    
    // Relationship getters and setters
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
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