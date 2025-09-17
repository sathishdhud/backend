package com.hotelworks.service;

import com.hotelworks.dto.request.RoomRequest;
import com.hotelworks.dto.response.RoomStatusResponse;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new room
     */
    public RoomStatusResponse createRoom(RoomRequest request) {
        // Check for duplicate room number first
        if (request.getRoomNo() != null) {
            Optional<Room> existingRoom = roomRepository.findByRoomNo(request.getRoomNo());
            if (existingRoom.isPresent()) {
                throw new RuntimeException("Room number already exists: " + request.getRoomNo() + 
                    " (Room ID: " + existingRoom.get().getRoomId() + ")");
            }
        }
        
        // Generate room ID if not provided
        String roomId = request.getRoomId();
        if (roomId == null || roomId.trim().isEmpty()) {
            roomId = numberGenerationService.generateRoomId();
        }
        
        // Validate room type if provided
        if (request.getRoomTypeId() != null && !roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new RuntimeException("Room type not found: " + request.getRoomTypeId());
        }
        
        // Check if room ID already exists
        if (roomRepository.existsById(roomId)) {
            throw new RuntimeException("Room ID already exists: " + roomId);
        }
        
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomNo(request.getRoomNo());
        room.setFloor(request.getFloor());
        room.setStatus(request.getStatus());
        room.setRoomTypeId(request.getRoomTypeId());
        
        Room savedRoom = roomRepository.save(room);
        return mapToRoomStatusResponse(savedRoom);
    }
    
    /**
     * Get all rooms with their current status and guest information
     */
    public List<RoomStatusResponse> getAllRoomsWithStatus() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
            .map(this::mapToRoomStatusResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get rooms by status
     */
    public List<RoomStatusResponse> getRoomsByStatus(String status) {
        List<Room> rooms = roomRepository.findByStatus(status);
        return rooms.stream()
            .map(this::mapToRoomStatusResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get available rooms (VR status)
     */
    public List<RoomStatusResponse> getAvailableRooms() {
        List<Room> rooms = roomRepository.findAvailableRooms();
        return rooms.stream()
            .map(this::mapToRoomStatusResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get occupied rooms (OD status)
     */
    public List<RoomStatusResponse> getOccupiedRooms() {
        List<Room> rooms = roomRepository.findOccupiedRooms();
        return rooms.stream()
            .map(this::mapToRoomStatusResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get rooms by floor
     */
    public List<RoomStatusResponse> getRoomsByFloor(String floor) {
        List<Room> rooms = roomRepository.findByFloor(floor);
        return rooms.stream()
            .map(this::mapToRoomStatusResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get room occupancy statistics
     */
    public RoomOccupancyStats getOccupancyStats() {
        Long totalRooms = roomRepository.count();
        Long occupiedRooms = roomRepository.countByStatus("OD");
        Long availableRooms = roomRepository.countByStatus("VR");
        Long blockedRooms = roomRepository.countByStatus("Blocked");
        Long outOfOrderRooms = roomRepository.countByStatus("OO");
        
        return new RoomOccupancyStats(totalRooms, occupiedRooms, availableRooms, blockedRooms, outOfOrderRooms);
    }
    
    /**
     * Get room by ID
     */
    public RoomStatusResponse getRoomById(String roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        return mapToRoomStatusResponse(room);
    }
    
    /**
     * Update room status
     */
    public RoomStatusResponse updateRoomStatus(String roomId, String status) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        
        room.setStatus(status);
        Room savedRoom = roomRepository.save(room);
        return mapToRoomStatusResponse(savedRoom);
    }
    
    /**
     * Update room details
     */
    public RoomStatusResponse updateRoom(String roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        
        // Validate room type if provided
        if (request.getRoomTypeId() != null && !roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new RuntimeException("Room type not found: " + request.getRoomTypeId());
        }
        
        // Check for duplicate room number (if room number is being changed)
        if (request.getRoomNo() != null && !request.getRoomNo().equals(room.getRoomNo())) {
            Optional<Room> existingRoom = roomRepository.findByRoomNo(request.getRoomNo());
            if (existingRoom.isPresent()) {
                throw new RuntimeException("Room number already exists: " + request.getRoomNo() + 
                    " (Room ID: " + existingRoom.get().getRoomId() + ")");
            }
        }
        
        // Update room details
        room.setRoomNo(request.getRoomNo());
        room.setFloor(request.getFloor());
        room.setRoomTypeId(request.getRoomTypeId());
        
        // Only update status if provided and room is not currently occupied
        if (request.getStatus() != null) {
            // Check if room is currently occupied before changing status
            if (("OD".equals(room.getStatus()) || "OI".equals(room.getStatus())) && 
                !("OD".equals(request.getStatus()) || "OI".equals(request.getStatus()))) {
                // Check if there are active check-ins
                List<CheckIn> activeCheckIns = checkInRepository.findByRoomId(roomId);
                if (!activeCheckIns.isEmpty()) {
                    CheckIn latestCheckIn = activeCheckIns.get(activeCheckIns.size() - 1);
                    LocalDate currentDate = LocalDate.now();
                    if (!latestCheckIn.getDepartureDate().isBefore(currentDate)) {
                        throw new RuntimeException("Cannot change status - room is currently occupied by guest");
                    }
                }
            }
            room.setStatus(request.getStatus());
        }
        
        Room savedRoom = roomRepository.save(room);
        return mapToRoomStatusResponse(savedRoom);
    }
    
    private RoomStatusResponse mapToRoomStatusResponse(Room room) {
        RoomStatusResponse response = new RoomStatusResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomNo(room.getRoomNo());
        response.setFloor(room.getFloor());
        response.setStatus(room.getStatus());
        response.setRoomTypeId(room.getRoomTypeId());
        
        // Fetch and set room type name by ID
        if (room.getRoomTypeId() != null) {
            roomTypeRepository.findById(room.getRoomTypeId())
                .ifPresent(roomType -> response.setRoomTypeName(roomType.getTypeName()));
        }
        
        // If room is occupied, get guest information
        if ("OD".equals(room.getStatus()) || "OI".equals(room.getStatus())) {
            List<CheckIn> checkIns = checkInRepository.findByRoomId(room.getRoomId());
            if (!checkIns.isEmpty()) {
                CheckIn currentCheckIn = checkIns.get(checkIns.size() - 1); // Get latest check-in
                LocalDate currentDate = LocalDate.now();
                
                // Check if guest is currently in house
                if (!currentCheckIn.getDepartureDate().isBefore(currentDate)) {
                    response.setGuestName(currentCheckIn.getGuestName());
                    response.setFolioNo(currentCheckIn.getFolioNo());
                }
            }
        }
        
        return response;
    }
    
    // Inner class for occupancy statistics
    public static class RoomOccupancyStats {
        private Long totalRooms;
        private Long occupiedRooms;
        private Long availableRooms;
        private Long blockedRooms;
        private Long outOfOrderRooms;
        private Double occupancyPercentage;
        
        public RoomOccupancyStats(Long totalRooms, Long occupiedRooms, Long availableRooms, 
                                 Long blockedRooms, Long outOfOrderRooms) {
            this.totalRooms = totalRooms;
            this.occupiedRooms = occupiedRooms;
            this.availableRooms = availableRooms;
            this.blockedRooms = blockedRooms;
            this.outOfOrderRooms = outOfOrderRooms;
            this.occupancyPercentage = totalRooms > 0 ? 
                (occupiedRooms.doubleValue() / totalRooms.doubleValue()) * 100 : 0.0;
        }
        
        // Getters
        public Long getTotalRooms() { return totalRooms; }
        public Long getOccupiedRooms() { return occupiedRooms; }
        public Long getAvailableRooms() { return availableRooms; }
        public Long getBlockedRooms() { return blockedRooms; }
        public Long getOutOfOrderRooms() { return outOfOrderRooms; }
        public Double getOccupancyPercentage() { return occupancyPercentage; }
    }
}