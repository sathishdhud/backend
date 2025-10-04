package com.hotelworks.service;

import com.hotelworks.dto.request.ReservationRequest;
import com.hotelworks.dto.response.DeletedReservationResponse;
import com.hotelworks.dto.response.ReservationResponse;
import com.hotelworks.entity.DeletedReservation;
import com.hotelworks.entity.Reservation;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.DeletedReservationRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.CompanyRepository;
import com.hotelworks.repository.PlanTypeRepository;
import com.hotelworks.repository.RoomTypeRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.BillSettlementTypeRepository;
import com.hotelworks.repository.ArrivalModeRepository;
import com.hotelworks.repository.NationalityRepository;
import com.hotelworks.repository.RefModeRepository;
import com.hotelworks.repository.ResvSourceRepository;
import com.hotelworks.repository.TaxationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private PlanTypeRepository planTypeRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
    // Additional repositories for new fields
    @Autowired
    private BillSettlementTypeRepository billSettlementTypeRepository;
    
    @Autowired
    private ArrivalModeRepository arrivalModeRepository;
    
    @Autowired
    private NationalityRepository nationalityRepository;
    
    @Autowired
    private RefModeRepository refModeRepository;
    
    @Autowired
    private ResvSourceRepository resvSourceRepository;
    
    @Autowired
    private TaxationRepository taxationRepository;
    
    @Autowired
    private DeletedReservationRepository deletedReservationRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Create a new reservation
     */
    public ReservationResponse createReservation(ReservationRequest request) {
        validateReservationRequest(request);
        
        // Check room availability for the requested dates if specific room type is requested
        if (request.getRoomTypeId() != null) {
            validateRoomAvailabilityForType(request.getRoomTypeId(), request.getArrivalDate(), request.getDepartureDate(), request.getNoOfRooms());
        }
        
        Reservation reservation = new Reservation();
        reservation.setReservationNo(numberGenerationService.generateReservationNumber());
        reservation.setGuestName(request.getGuestName());
        reservation.setCompanyId(request.getCompanyId());
        reservation.setPlanId(request.getPlanId());
        reservation.setRoomTypeId(request.getRoomTypeId());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        
        // Calculate number of days
        long days = ChronoUnit.DAYS.between(request.getArrivalDate(), request.getDepartureDate());
        reservation.setNoOfDays((int) days);
        
        reservation.setNoOfPersons(request.getNoOfPersons());
        reservation.setNoOfRooms(request.getNoOfRooms());
        reservation.setMobileNumber(request.getMobileNumber());
        reservation.setEmailId(request.getEmailId());
        
        // Set rate directly without tax calculation
        reservation.setRate(request.getRate());
        
        reservation.setIncludingGst(request.getIncludingGst());
        reservation.setRemarks(request.getRemarks());
        reservation.setRoomsCheckedIn(0);
        
        // Set ID proof fields
        reservation.setIdProof1(request.getIdProof1());
        reservation.setIdProof2(request.getIdProof2());
        reservation.setIdProof3(request.getIdProof3());
        
        // Set additional fields
        reservation.setSettlementTypeId(request.getSettlementTypeId());
        reservation.setArrivalModeId(request.getArrivalModeId());
        reservation.setArrivalDetails(request.getArrivalDetails());
        reservation.setNationalityId(request.getNationalityId());
        reservation.setRefModeId(request.getRefModeId());
        reservation.setResvSourceId(request.getResvSourceId());
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Send email confirmation if email is provided
        if (savedReservation.getEmailId() != null && !savedReservation.getEmailId().isEmpty()) {
            ReservationResponse response = mapToReservationResponse(savedReservation);
            emailService.sendDetailedReservationConfirmation(
                savedReservation.getEmailId(),
                response
            );
        }
        
        return mapToReservationResponse(savedReservation);
    }
    
    /**
     * Update an existing reservation
     */
    public ReservationResponse updateReservation(String reservationNo, ReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        // Check if any rooms are checked in
        if (reservation.getRoomsCheckedIn() > 0) {
            throw new RuntimeException("Cannot modify reservation with checked-in rooms");
        }
        
        validateReservationRequest(request);
        
        reservation.setGuestName(request.getGuestName());
        reservation.setCompanyId(request.getCompanyId());
        reservation.setPlanId(request.getPlanId());
        reservation.setRoomTypeId(request.getRoomTypeId());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        
        long days = ChronoUnit.DAYS.between(request.getArrivalDate(), request.getDepartureDate());
        reservation.setNoOfDays((int) days);
        
        reservation.setNoOfPersons(request.getNoOfPersons());
        reservation.setNoOfRooms(request.getNoOfRooms());
        reservation.setMobileNumber(request.getMobileNumber());
        reservation.setEmailId(request.getEmailId());
        
        // Set rate directly without tax calculation
        reservation.setRate(request.getRate());
        
        reservation.setIncludingGst(request.getIncludingGst());
        reservation.setRemarks(request.getRemarks());
        
        // Update ID proof fields
        reservation.setIdProof1(request.getIdProof1());
        reservation.setIdProof2(request.getIdProof2());
        reservation.setIdProof3(request.getIdProof3());
        
        // Update additional fields
        reservation.setSettlementTypeId(request.getSettlementTypeId());
        reservation.setArrivalModeId(request.getArrivalModeId());
        reservation.setArrivalDetails(request.getArrivalDetails());
        reservation.setNationalityId(request.getNationalityId());
        reservation.setRefModeId(request.getRefModeId());
        reservation.setResvSourceId(request.getResvSourceId());
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(savedReservation);
    }
    
    /**
     * Get all reservations
     */
    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get reservation by number
     */
    public ReservationResponse getReservation(String reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        return mapToReservationResponse(reservation);
    }
    
    /**
     * Search reservations
     */
    public List<ReservationResponse> searchReservations(String searchTerm) {
        List<Reservation> reservations = reservationRepository.searchReservations(searchTerm);
        return reservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get expected arrivals for a date
     */
    public List<ReservationResponse> getExpectedArrivals(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findExpectedArrivals(date);
        return reservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get expected departures for a date
     */
    public List<ReservationResponse> getExpectedDepartures(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findExpectedDepartures(date);
        return reservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get pending check-ins
     */
    public List<ReservationResponse> getPendingCheckIns() {
        List<Reservation> reservations = reservationRepository.findPendingCheckIns();
        return reservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Increment rooms checked in count
     */
    public void incrementRoomsCheckedIn(String reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        if (reservation.getRoomsCheckedIn() >= reservation.getNoOfRooms()) {
            throw new RuntimeException("All rooms for this reservation are already checked in");
        }
        
        reservation.setRoomsCheckedIn(reservation.getRoomsCheckedIn() + 1);
        reservationRepository.save(reservation);
    }
    
    private void validateReservationRequest(ReservationRequest request) {
        if (request.getArrivalDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Arrival date cannot be in the past");
        }
        
        if (request.getDepartureDate().isBefore(request.getArrivalDate()) || 
            request.getDepartureDate().isEqual(request.getArrivalDate())) {
            throw new RuntimeException("Departure date must be after arrival date");
        }
        
        // Validate referenced entities if provided
        if (request.getCompanyId() != null && !companyRepository.existsById(request.getCompanyId())) {
            throw new RuntimeException("Company not found: " + request.getCompanyId());
        }
        
        if (request.getPlanId() != null && !planTypeRepository.existsById(request.getPlanId())) {
            throw new RuntimeException("Plan type not found: " + request.getPlanId());
        }
        
        if (request.getRoomTypeId() != null && !roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new RuntimeException("Room type not found: " + request.getRoomTypeId());
        }
        
        // Validate additional referenced entities if provided
        if (request.getSettlementTypeId() != null && !billSettlementTypeRepository.existsById(request.getSettlementTypeId())) {
            throw new RuntimeException("Settlement type not found: " + request.getSettlementTypeId());
        }
        
        if (request.getArrivalModeId() != null && !arrivalModeRepository.existsById(request.getArrivalModeId())) {
            throw new RuntimeException("Arrival mode not found: " + request.getArrivalModeId());
        }
        
        if (request.getNationalityId() != null && !nationalityRepository.existsById(request.getNationalityId())) {
            throw new RuntimeException("Nationality not found: " + request.getNationalityId());
        }
        
        if (request.getRefModeId() != null && !refModeRepository.existsById(request.getRefModeId())) {
            throw new RuntimeException("Ref mode not found: " + request.getRefModeId());
        }
        
        // Only validate reservation source if it's provided
        if (request.getResvSourceId() != null && !request.getResvSourceId().trim().isEmpty()) {
            if (!resvSourceRepository.existsById(request.getResvSourceId())) {
                throw new RuntimeException("Reservation source not found: " + request.getResvSourceId());
            }
        }
    }
    
    /**
     * Validate room availability for a specific room type and dates
     */
    private void validateRoomAvailabilityForType(String roomTypeId, LocalDate arrivalDate, LocalDate departureDate, int requiredRooms) {
        // Get all rooms of the requested type
        List<Room> roomsOfType = roomRepository.findByRoomTypeId(roomTypeId);
        
        if (roomsOfType.isEmpty()) {
            throw new RuntimeException("No rooms found for room type: " + roomTypeId);
        }
        
        // Count available rooms for the requested dates
        int availableRooms = 0;
        for (Room room : roomsOfType) {
            if (roomStatusManagementService.isRoomAvailableForDates(room.getRoomId(), arrivalDate, departureDate)) {
                availableRooms++;
            }
        }
        
        if (availableRooms < requiredRooms) {
            throw new RuntimeException(String.format(
                "Insufficient rooms available. Requested: %d, Available: %d for room type: %s between %s and %s", 
                requiredRooms, availableRooms, roomTypeId, arrivalDate, departureDate));
        }
    }
    
    /**
     * Get available rooms for a specific room type and dates
     */
    public List<Room> getAvailableRoomsForType(String roomTypeId, LocalDate arrivalDate, LocalDate departureDate) {
        List<Room> roomsOfType = roomRepository.findByRoomTypeId(roomTypeId);
        
        return roomsOfType.stream()
            .filter(room -> roomStatusManagementService.isRoomAvailableForDates(room.getRoomId(), arrivalDate, departureDate))
            .collect(Collectors.toList());
    }
    
    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationNo(reservation.getReservationNo());
        response.setGuestName(reservation.getGuestName());
        response.setCompanyId(reservation.getCompanyId());
        response.setPlanId(reservation.getPlanId());
        response.setRoomTypeId(reservation.getRoomTypeId());
        response.setArrivalDate(reservation.getArrivalDate());
        response.setDepartureDate(reservation.getDepartureDate());
        response.setNoOfDays(reservation.getNoOfDays());
        response.setNoOfPersons(reservation.getNoOfPersons());
        response.setNoOfRooms(reservation.getNoOfRooms());
        response.setMobileNumber(reservation.getMobileNumber());
        response.setEmailId(reservation.getEmailId());
        response.setRate(reservation.getRate());
        response.setIncludingGst(reservation.getIncludingGst());
        response.setRemarks(reservation.getRemarks());
        response.setRoomsCheckedIn(reservation.getRoomsCheckedIn());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        
        // Set soft delete fields
        response.setDeleted(reservation.isDeleted());
        response.setDeletedAt(reservation.getDeletedAt());
        
        // Set ID proof fields
        response.setIdProof1(reservation.getIdProof1());
        response.setIdProof2(reservation.getIdProof2());
        response.setIdProof3(reservation.getIdProof3());
        
        // Set additional fields
        response.setSettlementTypeId(reservation.getSettlementTypeId());
        response.setArrivalModeId(reservation.getArrivalModeId());
        response.setArrivalDetails(reservation.getArrivalDetails());
        response.setNationalityId(reservation.getNationalityId());
        response.setRefModeId(reservation.getRefModeId());
        response.setResvSourceId(reservation.getResvSourceId());
        
        // Fetch and set related entity names by IDs
        if (reservation.getCompanyId() != null) {
            companyRepository.findById(reservation.getCompanyId())
                .ifPresent(company -> response.setCompanyName(company.getCompanyName()));
        }
        
        if (reservation.getPlanId() != null) {
            planTypeRepository.findById(reservation.getPlanId())
                .ifPresent(planType -> response.setPlanName(planType.getPlanName()));
        }
        
        if (reservation.getRoomTypeId() != null) {
            roomTypeRepository.findById(reservation.getRoomTypeId())
                .ifPresent(roomType -> response.setRoomTypeName(roomType.getTypeName()));
        }
        
        // Fetch and set names for additional related entities
        if (reservation.getSettlementTypeId() != null) {
            billSettlementTypeRepository.findById(reservation.getSettlementTypeId())
                .ifPresent(billSettlementType -> response.setSettlementTypeName(billSettlementType.getName()));
        }
        
        if (reservation.getArrivalModeId() != null) {
            arrivalModeRepository.findById(reservation.getArrivalModeId())
                .ifPresent(arrivalMode -> response.setArrivalModeName(arrivalMode.getArrivalMode()));
        }
        
        if (reservation.getNationalityId() != null) {
            nationalityRepository.findById(reservation.getNationalityId())
                .ifPresent(nationality -> response.setNationalityName(nationality.getNationality()));
        }
        
        if (reservation.getRefModeId() != null) {
            refModeRepository.findById(reservation.getRefModeId())
                .ifPresent(refMode -> response.setRefModeName(refMode.getRefMode()));
        }
        
        if (reservation.getResvSourceId() != null) {
            try {
                resvSourceRepository.findById(reservation.getResvSourceId())
                    .ifPresent(resvSource -> response.setResvSourceName(resvSource.getResvSource()));
            } catch (Exception e) {
                // Log the exception but don't fail the entire operation
                System.err.println("Warning: Error fetching reservation source name for ID: " + reservation.getResvSourceId() + ", Error: " + e.getMessage());
            }
        }
        
        return response;
    }
    
    /**
     * Check room availability for reservation dates
     */
    public boolean checkRoomAvailability(String roomTypeId, LocalDate arrivalDate, LocalDate departureDate, int requiredRooms) {
        try {
            validateRoomAvailabilityForType(roomTypeId, arrivalDate, departureDate, requiredRooms);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
    
    /**
     * Delete reservation by reservation number (move to deleted table)
     */
    public void deleteReservation(String reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        // Check if any rooms are checked in
        if (reservation.getRoomsCheckedIn() > 0) {
            throw new RuntimeException("Cannot delete reservation with checked-in rooms");
        }
        
        // Move reservation to deleted table
        DeletedReservation deletedReservation = new DeletedReservation(reservation);
        deletedReservationRepository.save(deletedReservation);
        
        // Remove from active reservations table
        reservationRepository.deleteById(reservationNo);
    }
    
    /**
     * Update rooms checked-in count for a reservation
     */
    public ReservationResponse updateRoomsCheckedIn(String reservationNo, int roomsCheckedIn) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        // Validate that roomsCheckedIn is not negative and not greater than total rooms
        if (roomsCheckedIn < 0) {
            throw new RuntimeException("Rooms checked-in count cannot be negative");
        }
        
        if (roomsCheckedIn > reservation.getNoOfRooms()) {
            throw new RuntimeException("Rooms checked-in count cannot exceed total rooms in reservation");
        }
        
        reservation.setRoomsCheckedIn(roomsCheckedIn);
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(savedReservation);
    }
    
    /**
     * Get all deleted reservations
     */
    public List<DeletedReservationResponse> getDeletedReservations() {
        List<DeletedReservation> deletedReservations = deletedReservationRepository.findAll();
        return deletedReservations.stream()
            .map(DeletedReservationResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Restore a deleted reservation
     */
    public ReservationResponse restoreReservation(String reservationNo) {
        DeletedReservation deletedReservation = deletedReservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Deleted reservation not found: " + reservationNo));
        
        // Create a new reservation from the deleted reservation
        Reservation reservation = new Reservation();
        reservation.setReservationNo(deletedReservation.getReservationNo());
        reservation.setGuestName(deletedReservation.getGuestName());
        reservation.setCompanyId(deletedReservation.getCompanyId());
        reservation.setPlanId(deletedReservation.getPlanId());
        reservation.setRoomTypeId(deletedReservation.getRoomTypeId());
        reservation.setArrivalDate(deletedReservation.getArrivalDate());
        reservation.setDepartureDate(deletedReservation.getDepartureDate());
        reservation.setNoOfDays(deletedReservation.getNoOfDays());
        reservation.setNoOfPersons(deletedReservation.getNoOfPersons());
        reservation.setNoOfRooms(deletedReservation.getNoOfRooms());
        reservation.setMobileNumber(deletedReservation.getMobileNumber());
        reservation.setEmailId(deletedReservation.getEmailId());
        reservation.setRate(deletedReservation.getRate());
        reservation.setIncludingGst(deletedReservation.getIncludingGst());
        reservation.setRemarks(deletedReservation.getRemarks());
        reservation.setRoomsCheckedIn(deletedReservation.getRoomsCheckedIn());
        reservation.setIdProof1(deletedReservation.getIdProof1());
        reservation.setIdProof2(deletedReservation.getIdProof2());
        reservation.setIdProof3(deletedReservation.getIdProof3());
        reservation.setSettlementTypeId(deletedReservation.getSettlementTypeId());
        reservation.setArrivalModeId(deletedReservation.getArrivalModeId());
        reservation.setArrivalDetails(deletedReservation.getArrivalDetails());
        reservation.setNationalityId(deletedReservation.getNationalityId());
        reservation.setRefModeId(deletedReservation.getRefModeId());
        reservation.setResvSourceId(deletedReservation.getResvSourceId());
        reservation.setCreatedAt(deletedReservation.getCreatedAt());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        // Save the restored reservation
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Remove from deleted table
        deletedReservationRepository.deleteById(reservationNo);
        
        return mapToReservationResponse(savedReservation);
    }
    
    /**
     * Send reservation confirmation email with attachment
     */
    public boolean sendReservationConfirmationEmail(String reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        if (reservation.getEmailId() == null || reservation.getEmailId().isEmpty()) {
            throw new RuntimeException("No email address provided for reservation: " + reservationNo);
        }
        
        ReservationResponse response = mapToReservationResponse(reservation);
        return emailService.sendDetailedReservationConfirmation(
            reservation.getEmailId(),
            response
        );
    }
}