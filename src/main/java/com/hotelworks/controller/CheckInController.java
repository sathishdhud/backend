package com.hotelworks.controller;

import com.hotelworks.dto.request.CheckInRequest;
import com.hotelworks.dto.response.CheckInResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@Tag(name = "Check-ins", description = "Hotel Check-in Management APIs")
public class CheckInController {
    
    @Autowired
    private CheckInService checkInService;
    
    @PostMapping
    @Operation(summary = "Process check-in", description = "Process guest check-in (reservation or walk-in)")
    public ResponseEntity<ApiResponse<CheckInResponse>> processCheckIn(
            @Valid @RequestBody CheckInRequest request) {
        try {
            CheckInResponse response = checkInService.processCheckIn(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Check-in processed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to process check-in: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{folioNo}")
    @Operation(summary = "Get check-in by folio number", description = "Retrieves check-in details by folio number")
    public ResponseEntity<ApiResponse<CheckInResponse>> getCheckIn(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            CheckInResponse response = checkInService.getCheckIn(folioNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Check-in not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get check-in by room", description = "Get check-in details for a specific room")
    public ResponseEntity<ApiResponse<CheckInResponse>> getCheckInByRoom(
            @Parameter(description = "Room ID") @PathVariable String roomId) {
        try {
            CheckInResponse response = checkInService.getCheckInByRoom(roomId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Check-in not found for room: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search check-ins", description = "Search check-ins by guest name, folio number, or room ID")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> searchCheckIns(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        try {
            List<CheckInResponse> responses = checkInService.searchCheckIns(searchTerm);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/inhouse")
    @Operation(summary = "Get in-house guests", description = "Get all currently in-house guests")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getInHouseGuests() {
        try {
            List<CheckInResponse> responses = checkInService.getInHouseGuests();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get in-house guests: " + e.getMessage()));
        }
    }
    
    @GetMapping("/checkouts/{date}")
    @Operation(summary = "Get expected checkouts", description = "Get expected checkouts for a specific date")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getExpectedCheckouts(
            @Parameter(description = "Checkout date (YYYY-MM-DD)") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<CheckInResponse> responses = checkInService.getExpectedCheckouts(date);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get expected checkouts: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{folioNo}")
    @Operation(summary = "Update check-in details", description = "Update check-in information (departure date, rate, remarks)")
    public ResponseEntity<ApiResponse<CheckInResponse>> updateCheckIn(
            @Parameter(description = "Folio number") @PathVariable String folioNo,
            @Valid @RequestBody CheckInRequest request) {
        try {
            CheckInResponse response = checkInService.updateCheckIn(folioNo, request);
            return ResponseEntity.ok(ApiResponse.success("Check-in updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update check-in: " + e.getMessage()));
        }
    }
}