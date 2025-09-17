package com.hotelworks.controller;

import com.hotelworks.dto.request.RoomRequest;
import com.hotelworks.dto.response.RoomStatusResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.RoomService;
import com.hotelworks.service.RoomStatusManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Hotel Room Management APIs")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
    @PostMapping
    @Operation(summary = "Create room", description = "Create a new room")
    public ResponseEntity<ApiResponse<RoomStatusResponse>> createRoom(
            @Valid @RequestBody RoomRequest request) {
        try {
            RoomStatusResponse response = roomService.createRoom(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create room: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all rooms with status", description = "Get all rooms with their current status and guest information")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getAllRoomsWithStatus() {
        try {
            List<RoomStatusResponse> responses = roomService.getAllRoomsWithStatus();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get rooms: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{roomId}")
    @Operation(summary = "Get room by ID", description = "Get specific room details by room ID")
    public ResponseEntity<ApiResponse<RoomStatusResponse>> getRoomById(
            @Parameter(description = "Room ID") @PathVariable String roomId) {
        try {
            RoomStatusResponse response = roomService.getRoomById(roomId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Room not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get rooms by status", description = "Get rooms filtered by status (VR, OD, OI, Blocked)")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getRoomsByStatus(
            @Parameter(description = "Room status (VR, OD, OI, Blocked)") @PathVariable String status) {
        try {
            List<RoomStatusResponse> responses = roomService.getRoomsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get rooms by status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available rooms", description = "Get all available rooms (VR status)")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getAvailableRooms() {
        try {
            List<RoomStatusResponse> responses = roomService.getAvailableRooms();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get available rooms: " + e.getMessage()));
        }
    }
    
    @GetMapping("/occupied")
    @Operation(summary = "Get occupied rooms", description = "Get all occupied rooms (OD status)")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getOccupiedRooms() {
        try {
            List<RoomStatusResponse> responses = roomService.getOccupiedRooms();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get occupied rooms: " + e.getMessage()));
        }
    }
    
    @GetMapping("/floor/{floor}")
    @Operation(summary = "Get rooms by floor", description = "Get rooms filtered by floor")
    public ResponseEntity<ApiResponse<List<RoomStatusResponse>>> getRoomsByFloor(
            @Parameter(description = "Floor number") @PathVariable String floor) {
        try {
            List<RoomStatusResponse> responses = roomService.getRoomsByFloor(floor);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get rooms by floor: " + e.getMessage()));
        }
    }
    
    @GetMapping("/occupancy-stats")
    @Operation(summary = "Get occupancy statistics", description = "Get room occupancy statistics")
    public ResponseEntity<ApiResponse<RoomService.RoomOccupancyStats>> getOccupancyStats() {
        try {
            RoomService.RoomOccupancyStats stats = roomService.getOccupancyStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get occupancy stats: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{roomId}/status/{status}")
    @Operation(summary = "Update room status", description = "Manual room status update (VR=Available, OD=Occupied Dirty, OI=Occupied Inspected, Blocked=Maintenance)")
    public ResponseEntity<ApiResponse<RoomStatusResponse>> updateRoomStatus(
            @Parameter(description = "Room ID") @PathVariable String roomId,
            @Parameter(description = "New status (VR, OD, OI, Blocked)") @PathVariable String status) {
        try {
            RoomStatusResponse response = roomService.updateRoomStatus(roomId, status);
            return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update room status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{roomId}")
    @Operation(summary = "Update room details", description = "Update room information (room number, floor, room type)")
    public ResponseEntity<ApiResponse<RoomStatusResponse>> updateRoom(
            @Parameter(description = "Room ID") @PathVariable String roomId,
            @Valid @RequestBody RoomRequest request) {
        try {
            RoomStatusResponse response = roomService.updateRoom(roomId, request);
            return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update room: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{roomId}/checkout")
    @Operation(summary = "Process room checkout", description = "Mark room as available after guest departure")
    public ResponseEntity<ApiResponse<String>> processRoomCheckout(
            @Parameter(description = "Room ID") @PathVariable String roomId,
            @Parameter(description = "Folio number for reference") @RequestParam(required = false) String folioNo) {
        try {
            boolean success = roomStatusManagementService.updateRoomStatusAfterDeparture(roomId, folioNo);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("Room checkout processed successfully - room is now available for new reservations"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process room checkout"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to process room checkout: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{roomId}/availability")
    @Operation(summary = "Check room availability", description = "Check if room is available for specific dates")
    public ResponseEntity<ApiResponse<Boolean>> checkRoomAvailability(
            @Parameter(description = "Room ID") @PathVariable String roomId,
            @Parameter(description = "Arrival date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
            @Parameter(description = "Departure date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        try {
            boolean available = roomStatusManagementService.isRoomAvailableForDates(roomId, arrivalDate, departureDate);
            String message = available ? "Room is available for the requested dates" : "Room is not available for the requested dates";
            return ResponseEntity.ok(ApiResponse.success(message, available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to check room availability: " + e.getMessage()));
        }
    }
    
    @PostMapping("/status/automatic-update")
    @Operation(summary = "Trigger automatic room status update", description = "Manually trigger automatic room status updates for departure dates")
    public ResponseEntity<ApiResponse<RoomStatusManagementService.RoomStatusUpdateResult>> triggerAutomaticUpdate() {
        try {
            RoomStatusManagementService.RoomStatusUpdateResult result = 
                roomStatusManagementService.processAutomaticRoomStatusUpdates();
            
            String message = String.format("Automatic room status update completed: %d successful, %d failed", 
                result.getSuccessfulUpdates(), result.getFailedUpdates());
            
            return ResponseEntity.ok(ApiResponse.success(message, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to trigger automatic update: " + e.getMessage()));
        }
    }
    
    @PostMapping("/status/overdue-check")
    @Operation(summary = "Check for overdue checkouts", description = "Manually check for guests who have overstayed their departure dates")
    public ResponseEntity<ApiResponse<RoomStatusManagementService.RoomStatusUpdateResult>> checkOverdueCheckouts() {
        try {
            RoomStatusManagementService.RoomStatusUpdateResult result = 
                roomStatusManagementService.processOverdueCheckouts();
            
            String message = result.getTotalCheckouts() > 0 ? 
                String.format("Found %d overdue checkouts requiring attention", result.getTotalCheckouts()) :
                "No overdue checkouts found";
            
            return ResponseEntity.ok(ApiResponse.success(message, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to check overdue checkouts: " + e.getMessage()));
        }
    }
}