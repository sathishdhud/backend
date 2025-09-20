package com.hotelworks.service;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.TaxationRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.service.NumberGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing automatic room status updates based on departure dates
 */
@Service
@Transactional
public class RoomStatusManagementService {
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private TaxationRepository taxationRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Process automatic room status updates for departure dates
     * This method should be called daily or triggered by scheduled jobs
     */
    public RoomStatusUpdateResult processAutomaticRoomStatusUpdates() {
        LocalDate currentDate = LocalDate.now();
        
        // Find all guests checking out today
        List<CheckIn> checkingOutToday = checkInRepository.findExpectedCheckouts(currentDate);
        
        RoomStatusUpdateResult result = new RoomStatusUpdateResult();
        result.setProcessingDate(currentDate);
        result.setTotalCheckouts(checkingOutToday.size());
        
        int successfulUpdates = 0;
        int failedUpdates = 0;
        
        for (CheckIn checkIn : checkingOutToday) {
            try {
                // Update room status to VR (Vacant Ready) for availability
                Room room = roomRepository.findById(checkIn.getRoomId()).orElse(null);
                if (room != null) {
                    // Only update if room is currently occupied
                    if ("OD".equals(room.getStatus()) || "OI".equals(room.getStatus())) {
                        room.setStatus("VR"); // Make room available for new reservations
                        roomRepository.save(room);
                        successfulUpdates++;
                        
                        result.addUpdatedRoom(room.getRoomId(), room.getRoomNo(), 
                                            checkIn.getGuestName(), "Available for new booking");
                    }
                } else {
                    failedUpdates++;
                    result.addFailedUpdate(checkIn.getRoomId(), 
                                         "Room not found: " + checkIn.getRoomId());
                }
            } catch (Exception e) {
                failedUpdates++;
                result.addFailedUpdate(checkIn.getRoomId(), 
                                     "Error updating room: " + e.getMessage());
            }
        }
        
        result.setSuccessfulUpdates(successfulUpdates);
        result.setFailedUpdates(failedUpdates);
        
        return result;
    }
    
    /**
     * Process automatic room status updates for overdue departures
     * Guests who should have checked out but room is still occupied
     */
    public RoomStatusUpdateResult processOverdueCheckouts() {
        LocalDate currentDate = LocalDate.now();
        
        // Find all guests who should have checked out before today but rooms are still occupied
        List<CheckIn> overdueCheckouts = checkInRepository.findOverdueCheckouts(currentDate);
        
        RoomStatusUpdateResult result = new RoomStatusUpdateResult();
        result.setProcessingDate(currentDate);
        result.setTotalCheckouts(overdueCheckouts.size());
        
        int successfulUpdates = 0;
        int failedUpdates = 0;
        
        for (CheckIn checkIn : overdueCheckouts) {
            try {
                Room room = roomRepository.findById(checkIn.getRoomId()).orElse(null);
                if (room != null && ("OD".equals(room.getStatus()) || "OI".equals(room.getStatus()))) {
                    // Mark as requiring attention - guest overstayed
                    room.setStatus("OD"); // Keep as occupied but flag for staff attention
                    roomRepository.save(room);
                    successfulUpdates++;
                    
                    result.addUpdatedRoom(room.getRoomId(), room.getRoomNo(), 
                                        checkIn.getGuestName(), 
                                        "OVERDUE - Guest should have checked out on " + checkIn.getDepartureDate());
                }
            } catch (Exception e) {
                failedUpdates++;
                result.addFailedUpdate(checkIn.getRoomId(), 
                                     "Error processing overdue checkout: " + e.getMessage());
            }
        }
        
        result.setSuccessfulUpdates(successfulUpdates);
        result.setFailedUpdates(failedUpdates);
        
        return result;
    }
    
    /**
     * Manually update room status after checkout/departure
     */
    public boolean updateRoomStatusAfterDeparture(String roomId, String folioNo) {
        try {
            Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
            
            // Update room status to VR (Vacant Ready)
            room.setStatus("VR");
            roomRepository.save(room);
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update room status after departure: " + e.getMessage());
        }
    }
    
    /**
     * Get current availability status for reservations
     */
    public boolean isRoomAvailableForDates(String roomId, LocalDate arrivalDate, LocalDate departureDate) {
        // Check if room exists and is in good condition
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null || "Blocked".equals(room.getStatus())) {
            return false;
        }
        
        // Check if there are any overlapping check-ins
        List<CheckIn> overlappingStays = checkInRepository.findOverlappingStays(
            roomId, arrivalDate, departureDate);
        
        return overlappingStays.isEmpty();
    }
    
    /**
     * Initialize required data for audit date processing
     * This method ensures required account heads and tax records exist
     */
    @PostConstruct
    public void initializeAuditData() {
        try {
            // Check and create Room Charges account head if it doesn't exist
            if (!hotelAccountHeadRepository.existsByName("Room Charges")) {
                HotelAccountHead roomChargesHead = new HotelAccountHead();
                roomChargesHead.setAccHeadId("ROOM_CHARGE");
                roomChargesHead.setName("Room Charges");
                hotelAccountHeadRepository.save(roomChargesHead);
                System.out.println("Created Room Charges account head");
            }
            
            // Check and create CGST account head if it doesn't exist
            if (!hotelAccountHeadRepository.existsByName("CGST")) {
                HotelAccountHead cgstHead = new HotelAccountHead();
                cgstHead.setAccHeadId("CGST");
                cgstHead.setName("CGST");
                hotelAccountHeadRepository.save(cgstHead);
                System.out.println("Created CGST account head");
            }
            
            // Check and create SGST account head if it doesn't exist
            if (!hotelAccountHeadRepository.existsByName("SGST")) {
                HotelAccountHead sgstHead = new HotelAccountHead();
                sgstHead.setAccHeadId("SGST");
                sgstHead.setName("SGST");
                hotelAccountHeadRepository.save(sgstHead);
                System.out.println("Created SGST account head");
            }
            
            // Check and create CGST tax record if it doesn't exist
            if (!taxationRepository.existsByTaxName("CGST")) {
                Taxation cgstTax = new Taxation();
                cgstTax.setTaxId("CGST001");
                cgstTax.setTaxName("CGST");
                cgstTax.setPercentage(new BigDecimal("9.00")); // 9% CGST
                taxationRepository.save(cgstTax);
                System.out.println("Created CGST tax record with 9% rate");
            }
            
            // Check and create SGST tax record if it doesn't exist
            if (!taxationRepository.existsByTaxName("SGST")) {
                Taxation sgstTax = new Taxation();
                sgstTax.setTaxId("SGST001");
                sgstTax.setTaxName("SGST");
                sgstTax.setPercentage(new BigDecimal("9.00")); // 9% SGST
                taxationRepository.save(sgstTax);
                System.out.println("Created SGST tax record with 9% rate");
            }
            
            System.out.println("Audit data initialization completed");
        } catch (Exception e) {
            System.err.println("Error during audit data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Result class for room status update operations
     */
    public static class RoomStatusUpdateResult {
        private LocalDate processingDate;
        private int totalCheckouts;
        private int successfulUpdates;
        private int failedUpdates;
        private List<UpdatedRoomInfo> updatedRooms = new java.util.ArrayList<>();
        private List<FailedUpdateInfo> failedUpdates_list = new java.util.ArrayList<>();
        
        // Getters and setters
        public LocalDate getProcessingDate() { return processingDate; }
        public void setProcessingDate(LocalDate processingDate) { this.processingDate = processingDate; }
        
        public int getTotalCheckouts() { return totalCheckouts; }
        public void setTotalCheckouts(int totalCheckouts) { this.totalCheckouts = totalCheckouts; }
        
        public int getSuccessfulUpdates() { return successfulUpdates; }
        public void setSuccessfulUpdates(int successfulUpdates) { this.successfulUpdates = successfulUpdates; }
        
        public int getFailedUpdates() { return failedUpdates; }
        public void setFailedUpdates(int failedUpdates) { this.failedUpdates = failedUpdates; }
        
        public List<UpdatedRoomInfo> getUpdatedRooms() { return updatedRooms; }
        public void setUpdatedRooms(List<UpdatedRoomInfo> updatedRooms) { this.updatedRooms = updatedRooms; }
        
        public List<FailedUpdateInfo> getFailedUpdatesList() { return failedUpdates_list; }
        public void setFailedUpdatesList(List<FailedUpdateInfo> failedUpdates_list) { this.failedUpdates_list = failedUpdates_list; }
        
        public void addUpdatedRoom(String roomId, String roomNo, String guestName, String notes) {
            updatedRooms.add(new UpdatedRoomInfo(roomId, roomNo, guestName, notes));
        }
        
        public void addFailedUpdate(String roomId, String error) {
            failedUpdates_list.add(new FailedUpdateInfo(roomId, error));
        }
    }
    
    public static class UpdatedRoomInfo {
        private String roomId;
        private String roomNo;
        private String guestName;
        private String notes;
        
        public UpdatedRoomInfo(String roomId, String roomNo, String guestName, String notes) {
            this.roomId = roomId;
            this.roomNo = roomNo;
            this.guestName = guestName;
            this.notes = notes;
        }
        
        // Getters
        public String getRoomId() { return roomId; }
        public String getRoomNo() { return roomNo; }
        public String getGuestName() { return guestName; }
        public String getNotes() { return notes; }
    }
    
    public static class FailedUpdateInfo {
        private String roomId;
        private String error;
        
        public FailedUpdateInfo(String roomId, String error) {
            this.roomId = roomId;
            this.error = error;
        }
        
        // Getters
        public String getRoomId() { return roomId; }
        public String getError() { return error; }
    }
}