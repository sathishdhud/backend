package com.hotelworks.controller;

import com.hotelworks.dto.request.RoomTypeRequest;
import com.hotelworks.dto.response.RoomTypeResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@Tag(name = "Room Types", description = "Room Type Management APIs")
public class RoomTypeController {
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @PostMapping
    @Operation(summary = "Create room type", description = "Create a new room type")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> createRoomType(
            @Valid @RequestBody RoomTypeRequest request) {
        try {
            RoomTypeResponse response = roomTypeService.createRoomType(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room type created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create room type: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all room types", description = "Get all available room types")
    public ResponseEntity<ApiResponse<List<RoomTypeResponse>>> getAllRoomTypes() {
        try {
            List<RoomTypeResponse> responses = roomTypeService.getAllRoomTypes();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get room types: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{typeId}")
    @Operation(summary = "Get room type by ID", description = "Get room type details by type ID")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> getRoomType(
            @Parameter(description = "Room Type ID") @PathVariable String typeId) {
        try {
            RoomTypeResponse response = roomTypeService.getRoomType(typeId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Room type not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{typeId}")
    @Operation(summary = "Update room type", description = "Update room type details")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> updateRoomType(
            @Parameter(description = "Room Type ID") @PathVariable String typeId,
            @Valid @RequestBody RoomTypeRequest request) {
        try {
            RoomTypeResponse response = roomTypeService.updateRoomType(typeId, request);
            return ResponseEntity.ok(ApiResponse.success("Room type updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update room type: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{typeId}")
    @Operation(summary = "Delete room type", description = "Delete room type by ID")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(
            @Parameter(description = "Room Type ID") @PathVariable String typeId) {
        try {
            roomTypeService.deleteRoomType(typeId);
            return ResponseEntity.ok(ApiResponse.success("Room type deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete room type: " + e.getMessage()));
        }
    }
}