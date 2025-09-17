package com.hotelworks.service;

import com.hotelworks.dto.request.RoomTypeRequest;
import com.hotelworks.dto.response.RoomTypeResponse;
import com.hotelworks.entity.RoomType;
import com.hotelworks.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomTypeService {
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new room type
     */
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {
        // Generate type ID if not provided
        String typeId = request.getTypeId();
        if (typeId == null || typeId.trim().isEmpty()) {
            typeId = numberGenerationService.generateRoomTypeId();
        }
        
        // Check if room type already exists
        if (roomTypeRepository.existsById(typeId)) {
            throw new RuntimeException("Room type already exists: " + typeId);
        }
        
        RoomType roomType = new RoomType();
        roomType.setTypeId(typeId);
        roomType.setTypeName(request.getTypeName());
        roomType.setNoOfRooms(request.getNoOfRooms());
        
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return mapToRoomTypeResponse(savedRoomType);
    }
    
    /**
     * Get all room types
     */
    public List<RoomTypeResponse> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return roomTypes.stream()
            .map(this::mapToRoomTypeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get room type by ID
     */
    public RoomTypeResponse getRoomType(String typeId) {
        RoomType roomType = roomTypeRepository.findById(typeId)
            .orElseThrow(() -> new RuntimeException("Room type not found: " + typeId));
        return mapToRoomTypeResponse(roomType);
    }
    
    /**
     * Update room type
     */
    public RoomTypeResponse updateRoomType(String typeId, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(typeId)
            .orElseThrow(() -> new RuntimeException("Room type not found: " + typeId));
        
        roomType.setTypeName(request.getTypeName());
        roomType.setNoOfRooms(request.getNoOfRooms());
        
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return mapToRoomTypeResponse(savedRoomType);
    }
    
    /**
     * Delete room type
     */
    public void deleteRoomType(String typeId) {
        if (!roomTypeRepository.existsById(typeId)) {
            throw new RuntimeException("Room type not found: " + typeId);
        }
        roomTypeRepository.deleteById(typeId);
    }
    
    private RoomTypeResponse mapToRoomTypeResponse(RoomType roomType) {
        RoomTypeResponse response = new RoomTypeResponse();
        response.setTypeId(roomType.getTypeId());
        response.setTypeName(roomType.getTypeName());
        response.setNoOfRooms(roomType.getNoOfRooms());
        return response;
    }
}