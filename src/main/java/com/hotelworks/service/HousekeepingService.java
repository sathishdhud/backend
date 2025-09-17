package com.hotelworks.service;

import com.hotelworks.dto.request.HousekeepingRequest;
import com.hotelworks.dto.response.HousekeepingResponse;
import com.hotelworks.dto.response.RoomStatusResponse;
import com.hotelworks.entity.Housekeeping;
import com.hotelworks.repository.HousekeepingRepository;
import com.hotelworks.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HousekeepingService {
    
    @Autowired
    private HousekeepingRepository housekeepingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomService roomService;
    
    /**
     * Create a new housekeeping task
     */
    public Housekeeping createHousekeepingTask(HousekeepingRequest request) {
        Housekeeping housekeeping = new Housekeeping();
        housekeeping.setRoomId(request.getRoomId());
        housekeeping.setStatus(request.getStatus());
        housekeeping.setNotes(request.getNotes());
        housekeeping.setAssignedTo(request.getAssignedTo());
        
        // Update the room status
        roomService.updateRoomStatus(request.getRoomId(), request.getStatus());
        
        return housekeepingRepository.save(housekeeping);
    }
    
    /**
     * Update housekeeping task status
     */
    public Housekeeping updateHousekeepingTask(Long taskId, HousekeepingRequest request) {
        Housekeeping housekeeping = housekeepingRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Housekeeping task not found: " + taskId));
        
        if (request.getStatus() != null) {
            housekeeping.setStatus(request.getStatus());
            // Update the room status
            roomService.updateRoomStatus(request.getRoomId(), request.getStatus());
        }
        
        if (request.getNotes() != null) {
            housekeeping.setNotes(request.getNotes());
        }
        
        if (request.getAssignedTo() != null) {
            housekeeping.setAssignedTo(request.getAssignedTo());
        }
        
        return housekeepingRepository.save(housekeeping);
    }
    
    /**
     * Get all housekeeping tasks
     */
    public List<Housekeeping> getAllHousekeepingTasks() {
        return housekeepingRepository.findAll();
    }
    
    /**
     * Get housekeeping tasks by room ID
     */
    public List<Housekeeping> getHousekeepingTasksByRoomId(String roomId) {
        return housekeepingRepository.findByRoomId(roomId);
    }
    
    /**
     * Get housekeeping tasks by status
     */
    public List<Housekeeping> getHousekeepingTasksByStatus(String status) {
        return housekeepingRepository.findByStatus(status);
    }
    
    /**
     * Get housekeeping tasks by assigned staff
     */
    public List<Housekeeping> getHousekeepingTasksByAssignedTo(String assignedTo) {
        return housekeepingRepository.findByAssignedTo(assignedTo);
    }
    
    /**
     * Get housekeeping task by ID
     */
    public Housekeeping getHousekeepingTaskById(Long taskId) {
        return housekeepingRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Housekeeping task not found: " + taskId));
    }
    
    /**
     * Delete housekeeping task
     */
    public void deleteHousekeepingTask(Long taskId) {
        if (!housekeepingRepository.existsById(taskId)) {
            throw new RuntimeException("Housekeeping task not found: " + taskId);
        }
        housekeepingRepository.deleteById(taskId);
    }
    
    /**
     * Map Housekeeping entity to HousekeepingResponse DTO
     */
    private HousekeepingResponse mapToHousekeepingResponse(Housekeeping housekeeping) {
        HousekeepingResponse response = new HousekeepingResponse();
        response.setTaskId(housekeeping.getTaskId());
        response.setRoomId(housekeeping.getRoomId());
        response.setStatus(housekeeping.getStatus());
        response.setAssignedTo(housekeeping.getAssignedTo());
        response.setNotes(housekeeping.getNotes());
        response.setCreatedAt(housekeeping.getCreatedAt());
        response.setUpdatedAt(housekeeping.getUpdatedAt());
        
        // Get room details
        try {
            RoomStatusResponse roomResponse = roomService.getRoomById(housekeeping.getRoomId());
            response.setRoomNo(roomResponse.getRoomNo());
            response.setFloor(roomResponse.getFloor());
        } catch (Exception e) {
            // If room not found, leave room details as null
        }
        
        return response;
    }
}