package com.hotelworks.service;

import com.hotelworks.dto.request.CheckInRequest;
import com.hotelworks.dto.response.CheckInResponse;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.AdvanceRepository;
import com.hotelworks.repository.ArrivalModeRepository;
import com.hotelworks.repository.CompanyRepository;
import com.hotelworks.repository.PlanTypeRepository;
import com.hotelworks.repository.RoomTypeRepository;
import com.hotelworks.repository.BillSettlementTypeRepository;
import com.hotelworks.repository.NationalityRepository;
import com.hotelworks.repository.RefModeRepository;
import com.hotelworks.repository.ResvSourceRepository;
import com.hotelworks.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckInService {
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private ArrivalModeRepository arrivalModeRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private PlanTypeRepository planTypeRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private BillSettlementTypeRepository settlementTypeRepository;
    
    @Autowired
    private NationalityRepository nationalityRepository;
    
    @Autowired
    private RefModeRepository refModeRepository;
    
    @Autowired
    private ResvSourceRepository resvSourceRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Process check-in for a guest
     */
    public CheckInResponse processCheckIn(CheckInRequest request) {
        validateCheckInRequest(request);
        
        // Auto-populate guest information from reservation if available
        populateGuestInfoFromReservation(request);
        
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo(numberGenerationService.generateFolioNumber());
        checkIn.setReservationNo(request.getReservationNo());
        checkIn.setGuestName(request.getGuestName());
        checkIn.setRoomId(request.getRoomId());
        checkIn.setArrivalDate(request.getArrivalDate());
        checkIn.setDepartureDate(request.getDepartureDate());
        checkIn.setMobileNumber(request.getMobileNumber());
        checkIn.setEmailId(request.getEmailId());
        checkIn.setRate(request.getRate());
        checkIn.setRemarks(request.getRemarks());
        checkIn.setAuditDate(LocalDate.now());
        checkIn.setWalkIn(request.getWalkIn());
        
        // Set ID proof fields
        checkIn.setIdProof1(request.getIdProof1());
        checkIn.setIdProof2(request.getIdProof2());
        checkIn.setIdProof3(request.getIdProof3());
        
        // Set additional fields
        if (request.getCompanyId() != null && !request.getCompanyId().trim().isEmpty()) {
            checkIn.setCompanyId(request.getCompanyId());
        }
        
        if (request.getPlanId() != null && !request.getPlanId().trim().isEmpty()) {
            checkIn.setPlanId(request.getPlanId());
        }
        
        if (request.getRoomTypeId() != null && !request.getRoomTypeId().trim().isEmpty()) {
            checkIn.setRoomTypeId(request.getRoomTypeId());
        }
        
        if (request.getSettlementTypeId() != null && !request.getSettlementTypeId().trim().isEmpty()) {
            checkIn.setSettlementTypeId(request.getSettlementTypeId());
        }
        
        if (request.getArrivalModeId() != null && !request.getArrivalModeId().trim().isEmpty()) {
            checkIn.setArrivalModeId(request.getArrivalModeId());
        } else if (request.getArrivalModeId() != null && request.getArrivalModeId().trim().isEmpty()) {
            // If arrivalModeId is explicitly set to empty string, set it to null
            checkIn.setArrivalModeId(null);
        }
        
        if (request.getArrivalDetails() != null && !request.getArrivalDetails().trim().isEmpty()) {
            checkIn.setArrivalDetails(request.getArrivalDetails());
        }
        
        if (request.getNationalityId() != null && !request.getNationalityId().trim().isEmpty()) {
            checkIn.setNationalityId(request.getNationalityId());
        }
        
        if (request.getRefModeId() != null && !request.getRefModeId().trim().isEmpty()) {
            checkIn.setRefModeId(request.getRefModeId());
        }
        
        if (request.getResvSourceId() != null && !request.getResvSourceId().trim().isEmpty()) {
            checkIn.setResvSourceId(request.getResvSourceId());
        }
        
        // Save check-in
        CheckIn savedCheckIn = checkInRepository.save(checkIn);
        
        // Update room status to Occupied Dirty (OD)
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Room not found: " + request.getRoomId()));
        room.setStatus("OD");
        roomRepository.save(room);
        
        // If not a walk-in, increment reservation check-in count
        if (!"Y".equals(request.getWalkIn()) && request.getReservationNo() != null) {
            reservationService.incrementRoomsCheckedIn(request.getReservationNo());
        }
        
        // Send email confirmation if email is provided
        if (savedCheckIn.getEmailId() != null && !savedCheckIn.getEmailId().isEmpty()) {
            emailService.sendCheckInConfirmation(
                savedCheckIn.getEmailId(),
                savedCheckIn.getGuestName(),
                savedCheckIn.getFolioNo()
            );
        }
        
        return mapToCheckInResponse(savedCheckIn);
    }
    
    /**
     * Get check-in by folio number
     */
    public CheckInResponse getCheckIn(String folioNo) {
        CheckIn checkIn = checkInRepository.findById(folioNo)
            .orElseThrow(() -> new RuntimeException("Check-in not found: " + folioNo));
        return mapToCheckInResponse(checkIn);
    }
    
    /**
     * Get check-in by room ID
     */
    public CheckInResponse getCheckInByRoom(String roomId) {
        List<CheckIn> checkIns = checkInRepository.findByRoomId(roomId);
        if (checkIns.isEmpty()) {
            throw new RuntimeException("No check-in found for room: " + roomId);
        }
        // Return the latest check-in for the room
        CheckIn checkIn = checkIns.get(checkIns.size() - 1);
        return mapToCheckInResponse(checkIn);
    }
    
    /**
     * Search check-ins
     */
    public List<CheckInResponse> searchCheckIns(String searchTerm) {
        List<CheckIn> checkIns = checkInRepository.searchCheckIns(searchTerm);
        return checkIns.stream()
            .map(this::mapToCheckInResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get in-house guests
     */
    public List<CheckInResponse> getInHouseGuests() {
        LocalDate currentDate = LocalDate.now();
        List<CheckIn> checkIns = checkInRepository.findInHouseGuests(currentDate);
        return checkIns.stream()
            .map(this::mapToCheckInResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get expected checkouts for a date
     */
    public List<CheckInResponse> getExpectedCheckouts(LocalDate date) {
        List<CheckIn> checkIns = checkInRepository.findExpectedCheckouts(date);
        return checkIns.stream()
            .map(this::mapToCheckInResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Update check-in details
     */
    public CheckInResponse updateCheckIn(String folioNo, CheckInRequest request) {
        CheckIn checkIn = checkInRepository.findById(folioNo)
            .orElseThrow(() -> new RuntimeException("Check-in not found: " + folioNo));
        
        // Validate dates if being updated
        if (request.getDepartureDate() != null) {
            if (request.getDepartureDate().isBefore(checkIn.getArrivalDate()) || 
                request.getDepartureDate().isEqual(checkIn.getArrivalDate())) {
                throw new RuntimeException("Departure date must be after arrival date");
            }
            checkIn.setDepartureDate(request.getDepartureDate());
        }
        
        // Update other fields if provided
        if (request.getRate() != null) {
            checkIn.setRate(request.getRate());
        }
        
        if (request.getRemarks() != null) {
            checkIn.setRemarks(request.getRemarks());
        }
        
        if (request.getMobileNumber() != null) {
            checkIn.setMobileNumber(request.getMobileNumber());
        }
        
        if (request.getEmailId() != null) {
            checkIn.setEmailId(request.getEmailId());
        }
        
        // Update ID proof fields if provided
        if (request.getIdProof1() != null) {
            checkIn.setIdProof1(request.getIdProof1());
        }
        
        if (request.getIdProof2() != null) {
            checkIn.setIdProof2(request.getIdProof2());
        }
        
        if (request.getIdProof3() != null) {
            checkIn.setIdProof3(request.getIdProof3());
        }
        
        // Update additional fields if provided
        if (request.getCompanyId() != null) {
            checkIn.setCompanyId(request.getCompanyId());
        }
        
        if (request.getPlanId() != null) {
            checkIn.setPlanId(request.getPlanId());
        }
        
        if (request.getRoomTypeId() != null) {
            checkIn.setRoomTypeId(request.getRoomTypeId());
        }
        
        if (request.getSettlementTypeId() != null) {
            checkIn.setSettlementTypeId(request.getSettlementTypeId());
        }
        
        if (request.getArrivalModeId() != null) {
            checkIn.setArrivalModeId(request.getArrivalModeId());
        } else if (request.getArrivalModeId() != null && request.getArrivalModeId().trim().isEmpty()) {
            // If arrivalModeId is explicitly set to empty string, set it to null
            checkIn.setArrivalModeId(null);
        }
        
        if (request.getArrivalDetails() != null) {
            checkIn.setArrivalDetails(request.getArrivalDetails());
        }
        
        if (request.getNationalityId() != null) {
            checkIn.setNationalityId(request.getNationalityId());
        }
        
        if (request.getRefModeId() != null) {
            checkIn.setRefModeId(request.getRefModeId());
        }
        
        if (request.getResvSourceId() != null) {
            checkIn.setResvSourceId(request.getResvSourceId());
        }
        
        CheckIn savedCheckIn = checkInRepository.save(checkIn);
        return mapToCheckInResponse(savedCheckIn);
    }
    
    private void validateCheckInRequest(CheckInRequest request) {
        // Check if room exists and is available
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Room not found: " + request.getRoomId()));
        
        // Enhanced room availability check using room status management service
        if (!roomStatusManagementService.isRoomAvailableForDates(request.getRoomId(), request.getArrivalDate(), request.getDepartureDate())) {
            throw new RuntimeException("Room is not available for the requested dates: " + request.getRoomId());
        }
        
        // Additional check for current room status - must be VR (Vacant Ready)
        if (!"VR".equals(room.getStatus())) {
            throw new RuntimeException("Room is not available for check-in. Current status: " + room.getStatus() + " (Room: " + request.getRoomId() + ")");
        }
        
        // If not a walk-in, validate reservation
        if (!"Y".equals(request.getWalkIn()) && request.getReservationNo() != null) {
            if (!reservationRepository.existsById(request.getReservationNo())) {
                throw new RuntimeException("Reservation not found: " + request.getReservationNo());
            }
        }
        
        // For walk-ins, guest name is required
        if ("Y".equals(request.getWalkIn()) && (request.getGuestName() == null || request.getGuestName().trim().isEmpty())) {
            throw new RuntimeException("Guest name is required for walk-in check-ins");
        }
        
        // Validate dates
        if (request.getDepartureDate().isBefore(request.getArrivalDate()) || 
            request.getDepartureDate().isEqual(request.getArrivalDate())) {
            throw new RuntimeException("Departure date must be after arrival date");
        }
        
        // Validate arrival date is not in the past
        if (request.getArrivalDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Arrival date cannot be in the past");
        }
        
        // Validate foreign key relationships if provided
        if (request.getArrivalModeId() != null && !request.getArrivalModeId().trim().isEmpty()) {
            if (!arrivalModeRepository.existsById(request.getArrivalModeId())) {
                throw new RuntimeException("Invalid arrival mode ID: " + request.getArrivalModeId());
            }
        }
        
        if (request.getCompanyId() != null && !request.getCompanyId().trim().isEmpty()) {
            if (!companyRepository.existsById(request.getCompanyId())) {
                throw new RuntimeException("Invalid company ID: " + request.getCompanyId());
            }
        }
        
        if (request.getPlanId() != null && !request.getPlanId().trim().isEmpty()) {
            if (!planTypeRepository.existsById(request.getPlanId())) {
                throw new RuntimeException("Invalid plan type ID: " + request.getPlanId());
            }
        }
        
        if (request.getRoomTypeId() != null && !request.getRoomTypeId().trim().isEmpty()) {
            if (!roomTypeRepository.existsById(request.getRoomTypeId())) {
                throw new RuntimeException("Invalid room type ID: " + request.getRoomTypeId());
            }
        }
        
        if (request.getSettlementTypeId() != null && !request.getSettlementTypeId().trim().isEmpty()) {
            if (!settlementTypeRepository.existsById(request.getSettlementTypeId())) {
                throw new RuntimeException("Invalid settlement type ID: " + request.getSettlementTypeId());
            }
        }
        
        if (request.getNationalityId() != null && !request.getNationalityId().trim().isEmpty()) {
            if (!nationalityRepository.existsById(request.getNationalityId())) {
                throw new RuntimeException("Invalid nationality ID: " + request.getNationalityId());
            }
        }
        
        if (request.getRefModeId() != null && !request.getRefModeId().trim().isEmpty()) {
            if (!refModeRepository.existsById(request.getRefModeId())) {
                throw new RuntimeException("Invalid reference mode ID: " + request.getRefModeId());
            }
        }
        
        if (request.getResvSourceId() != null && !request.getResvSourceId().trim().isEmpty()) {
            if (!resvSourceRepository.existsById(request.getResvSourceId())) {
                throw new RuntimeException("Invalid reservation source ID: " + request.getResvSourceId());
            }
        }
    }
    
    /**
     * Auto-populate guest information from reservation if available
     */
    private void populateGuestInfoFromReservation(CheckInRequest request) {
        // If reservation number is provided and guest name is not provided, fetch from reservation
        if (request.getReservationNo() != null && !request.getReservationNo().trim().isEmpty()) {
            Reservation reservation = reservationRepository.findById(request.getReservationNo())
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + request.getReservationNo()));
            
            // Auto-populate guest name if not provided
            if (request.getGuestName() == null || request.getGuestName().trim().isEmpty()) {
                request.setGuestName(reservation.getGuestName());
            }
            
            // Auto-populate other fields from reservation if not provided
            if (request.getMobileNumber() == null || request.getMobileNumber().trim().isEmpty()) {
                request.setMobileNumber(reservation.getMobileNumber());
            }
            
            if (request.getEmailId() == null || request.getEmailId().trim().isEmpty()) {
                request.setEmailId(reservation.getEmailId());
            }
            
            if (request.getRate() == null) {
                request.setRate(reservation.getRate());
            }
            
            // Use reservation dates if not provided
            if (request.getArrivalDate() == null) {
                request.setArrivalDate(reservation.getArrivalDate());
            }
            
            if (request.getDepartureDate() == null) {
                request.setDepartureDate(reservation.getDepartureDate());
            }
            
            // Auto-populate ID proof fields if not provided
            if (request.getIdProof1() == null) {
                request.setIdProof1(reservation.getIdProof1());
            }
            
            if (request.getIdProof2() == null) {
                request.setIdProof2(reservation.getIdProof2());
            }
            
            if (request.getIdProof3() == null) {
                request.setIdProof3(reservation.getIdProof3());
            }
            
            // Auto-populate additional fields if not provided
            if (request.getCompanyId() == null) {
                request.setCompanyId(reservation.getCompanyId());
            }
            
            if (request.getPlanId() == null) {
                request.setPlanId(reservation.getPlanId());
            }
            
            if (request.getRoomTypeId() == null) {
                request.setRoomTypeId(reservation.getRoomTypeId());
            }
            
            if (request.getSettlementTypeId() == null) {
                request.setSettlementTypeId(reservation.getSettlementTypeId());
            }
            
            if (request.getArrivalModeId() == null) {
                request.setArrivalModeId(reservation.getArrivalModeId());
            }
            
            if (request.getArrivalDetails() == null) {
                request.setArrivalDetails(reservation.getArrivalDetails());
            }
            
            if (request.getNationalityId() == null) {
                request.setNationalityId(reservation.getNationalityId());
            }
            
            if (request.getRefModeId() == null) {
                request.setRefModeId(reservation.getRefModeId());
            }
            
            if (request.getResvSourceId() == null) {
                request.setResvSourceId(reservation.getResvSourceId());
            }
        }
    }
    
    private CheckInResponse mapToCheckInResponse(CheckIn checkIn) {
        CheckInResponse response = new CheckInResponse();
        response.setFolioNo(checkIn.getFolioNo());
        response.setReservationNo(checkIn.getReservationNo());
        response.setGuestName(checkIn.getGuestName());
        response.setRoomId(checkIn.getRoomId());
        response.setArrivalDate(checkIn.getArrivalDate());
        response.setDepartureDate(checkIn.getDepartureDate());
        response.setMobileNumber(checkIn.getMobileNumber());
        response.setEmailId(checkIn.getEmailId());
        response.setRate(checkIn.getRate());
        response.setRemarks(checkIn.getRemarks());
        response.setAuditDate(checkIn.getAuditDate());
        response.setWalkIn(checkIn.getWalkIn());
        
        // Set ID proof fields
        response.setIdProof1(checkIn.getIdProof1());
        response.setIdProof2(checkIn.getIdProof2());
        response.setIdProof3(checkIn.getIdProof3());
        
        // Set additional fields
        response.setCompanyId(checkIn.getCompanyId());
        response.setPlanId(checkIn.getPlanId());
        response.setRoomTypeId(checkIn.getRoomTypeId());
        response.setSettlementTypeId(checkIn.getSettlementTypeId());
        response.setArrivalModeId(checkIn.getArrivalModeId());
        response.setArrivalDetails(checkIn.getArrivalDetails());
        response.setNationalityId(checkIn.getNationalityId());
        response.setRefModeId(checkIn.getRefModeId());
        response.setResvSourceId(checkIn.getResvSourceId());
        
        // Set room number by fetching from repository (consistent pattern)
        if (checkIn.getRoomId() != null) {
            roomRepository.findById(checkIn.getRoomId())
                .ifPresent(room -> response.setRoomNo(room.getRoomNo()));
        }
        
        // Calculate total advances
        BigDecimal totalAdvances = advanceRepository.getTotalAdvancesByFolio(checkIn.getFolioNo());
        response.setTotalAdvances(totalAdvances != null ? totalAdvances : BigDecimal.ZERO);
        
        return response;
    }
}