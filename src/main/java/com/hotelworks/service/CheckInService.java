package com.hotelworks.service;

import com.hotelworks.dto.request.CheckInRequest;
import com.hotelworks.dto.response.CheckInResponse;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.AdvanceRepository;
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
    private ReservationService reservationService;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
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