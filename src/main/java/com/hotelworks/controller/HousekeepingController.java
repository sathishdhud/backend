package com.hotelworks.controller;

import com.hotelworks.dto.request.HousekeepingRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.HousekeepingResponse;
import com.hotelworks.dto.response.RoomStatusResponse;
import com.hotelworks.entity.Housekeeping;
import com.hotelworks.service.HousekeepingService;
import com.hotelworks.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/housekeeping")
@Tag(name = "Housekeeping", description = "Housekeeping Management APIs")
public class HousekeepingController {
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private HousekeepingService housekeepingService;
    
    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get housekeeping task by ID", description = "Get a specific housekeeping task by its ID")
    public ResponseEntity<ApiResponse<Housekeeping>> getHousekeepingTaskById(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {
        try {
            Housekeeping task = housekeepingService.getHousekeepingTaskById(taskId);
            return ResponseEntity.ok(ApiResponse.success(task));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get housekeeping task: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tasks")
    @Operation(summary = "Get housekeeping tasks", description = "Get all housekeeping tasks for staff")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getHousekeepingTasks() {
        try {
            // Get all rooms that need housekeeping attention
            // This includes occupied rooms that need cleaning after checkout (OI status)
            // and available rooms that need preparation (VR status)
            List<RoomStatusResponse> roomsNeedingAttention = roomService.getAllRoomsWithStatus();
            
            // Filter to only include rooms that housekeeping staff would work on
            // Typically rooms with "OI" (Occupied In-house) or "VR" (Vacant Ready) status
            List<RoomStatusResponse> housekeepingTasks = roomsNeedingAttention.stream()
                .filter(room -> "OI".equals(room.getStatus()) || "VR".equals(room.getStatus()) || 
                               "OD".equals(room.getStatus()) || "Blocked".equals(room.getStatus()))
                .toList();
            
            return ResponseEntity.ok(ApiResponse.success(housekeepingTasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get housekeeping tasks: " + e.getMessage()));
        }
    }
    
    @PostMapping("/tasks")
    @Operation(summary = "Create housekeeping task", description = "Create a new housekeeping task with JSON payload")
    public ResponseEntity<ApiResponse<Housekeeping>> createHousekeepingTask(
            @Valid @RequestBody HousekeepingRequest request) {
        try {
            Housekeeping task = housekeepingService.createHousekeepingTask(request);
            return ResponseEntity.ok(ApiResponse.success("Housekeeping task created successfully", task));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create housekeeping task: " + e.getMessage()));
        }
    }
    
    @PutMapping("/tasks/{taskId}")
    @Operation(summary = "Update housekeeping task", description = "Update housekeeping task status with JSON payload")
    public ResponseEntity<ApiResponse<Housekeeping>> updateHousekeepingTask(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Valid @RequestBody HousekeepingRequest request) {
        try {
            Housekeeping task = housekeepingService.updateHousekeepingTask(taskId, request);
            return ResponseEntity.ok(ApiResponse.success("Housekeeping task updated successfully", task));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update housekeeping task: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "Delete housekeeping task", description = "Delete a housekeeping task by ID")
    public ResponseEntity<ApiResponse<String>> deleteHousekeepingTask(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {
        try {
            housekeepingService.deleteHousekeepingTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Housekeeping task deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete housekeeping task: " + e.getMessage()));
        }
    }
    
    @PostMapping("/room-status")
    @Operation(summary = "Update room cleaning status", description = "Update room status after cleaning")
    public ResponseEntity<ApiResponse<RoomStatusResponse>> updateRoomCleaningStatus(
            @RequestParam String roomId,
            @RequestParam String status) {
        try {
            // Update the room status based on housekeeping completion
            RoomStatusResponse updatedRoom = roomService.updateRoomStatus(roomId, status);
            return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", updatedRoom));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update room status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/rooms-by-status")
    @Operation(summary = "Get rooms by cleaning status", description = "Get rooms filtered by their cleaning status")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getRoomsByCleaningStatus(
            @RequestParam String status) {
        try {
            List<RoomStatusResponse> rooms = roomService.getRoomsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get rooms by status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get housekeeping statistics", description = "Get statistics about room statuses")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHousekeepingStatistics() {
        try {
            RoomService.RoomOccupancyStats stats = roomService.getOccupancyStats();
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalRooms", stats.getTotalRooms());
            statistics.put("occupiedRooms", stats.getOccupiedRooms());
            statistics.put("availableRooms", stats.getAvailableRooms());
            statistics.put("blockedRooms", stats.getBlockedRooms());
            statistics.put("outOfOrderRooms", stats.getOutOfOrderRooms());
            statistics.put("occupancyPercentage", stats.getOccupancyPercentage());
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get statistics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tasks/room/{roomId}")
    @Operation(summary = "Get housekeeping tasks by room", description = "Get all housekeeping tasks for a specific room")
    public ResponseEntity<ApiResponse<List<Housekeeping>>> getTasksByRoom(
            @Parameter(description = "Room ID") @PathVariable String roomId) {
        try {
            List<Housekeeping> tasks = housekeepingService.getHousekeepingTasksByRoomId(roomId);
            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get tasks for room: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tasks/status/{status}")
    @Operation(summary = "Get housekeeping tasks by status", description = "Get all housekeeping tasks with a specific status")
    public ResponseEntity<ApiResponse<List<Housekeeping>>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable String status) {
        try {
            List<Housekeeping> tasks = housekeepingService.getHousekeepingTasksByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get tasks by status: " + e.getMessage()));
        }
    }
}