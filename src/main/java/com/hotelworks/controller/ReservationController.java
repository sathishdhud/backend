package com.hotelworks.controller;

import com.hotelworks.dto.request.ReservationRequest;
import com.hotelworks.dto.response.ReservationResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.ReservationService;
import com.hotelworks.entity.Room;
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
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Hotel Reservation Management APIs")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping
    @Operation(summary = "Get all reservations", description = "Retrieves all hotel reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {
        try {
            List<ReservationResponse> responses = reservationService.getAllReservations();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve reservations: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Creates a new hotel reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create reservation: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{reservationNo}")
    @Operation(summary = "Get reservation by number", description = "Retrieves a reservation by its number")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            ReservationResponse response = reservationService.getReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Reservation not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{reservationNo}")
    @Operation(summary = "Update reservation", description = "Updates an existing reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo,
            @Valid @RequestBody ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.updateReservation(reservationNo, request);
            return ResponseEntity.ok(ApiResponse.success("Reservation updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update reservation: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search reservations", description = "Search reservations by guest name, mobile number, or reservation number")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> searchReservations(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        try {
            List<ReservationResponse> responses = reservationService.searchReservations(searchTerm);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/arrivals/{date}")
    @Operation(summary = "Get expected arrivals", description = "Get expected arrivals for a specific date")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getExpectedArrivals(
            @Parameter(description = "Arrival date (YYYY-MM-DD)") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ReservationResponse> responses = reservationService.getExpectedArrivals(date);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get expected arrivals: " + e.getMessage()));
        }
    }
    
    @GetMapping("/departures/{date}")
    @Operation(summary = "Get expected departures", description = "Get expected departures for a specific date")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getExpectedDepartures(
            @Parameter(description = "Departure date (YYYY-MM-DD)") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ReservationResponse> responses = reservationService.getExpectedDepartures(date);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get expected departures: " + e.getMessage()));
        }
    }
    
    @GetMapping("/pending-checkins")
    @Operation(summary = "Get pending check-ins", description = "Get reservations with pending check-ins")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getPendingCheckIns() {
        try {
            List<ReservationResponse> responses = reservationService.getPendingCheckIns();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get pending check-ins: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-availability")
    @Operation(summary = "Check room availability", description = "Check if sufficient rooms are available for specific room type and dates")
    public ResponseEntity<ApiResponse<Boolean>> checkRoomAvailability(
            @Parameter(description = "Room type ID") @RequestParam String roomTypeId,
            @Parameter(description = "Arrival date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
            @Parameter(description = "Departure date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @Parameter(description = "Number of rooms required") @RequestParam int requiredRooms) {
        try {
            boolean available = reservationService.checkRoomAvailability(roomTypeId, arrivalDate, departureDate, requiredRooms);
            String message = available ? 
                String.format("Sufficient rooms available (%d requested for %s)", requiredRooms, roomTypeId) :
                String.format("Insufficient rooms available (%d requested for %s)", requiredRooms, roomTypeId);
            return ResponseEntity.ok(ApiResponse.success(message, available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to check availability: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available-rooms")
    @Operation(summary = "Get available rooms", description = "Get list of available rooms for specific room type and dates")
    public ResponseEntity<ApiResponse<List<Room>>> getAvailableRooms(
            @Parameter(description = "Room type ID") @RequestParam String roomTypeId,
            @Parameter(description = "Arrival date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
            @Parameter(description = "Departure date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        try {
            List<Room> availableRooms = reservationService.getAvailableRoomsForType(roomTypeId, arrivalDate, departureDate);
            String message = String.format("Found %d available rooms for %s between %s and %s", 
                availableRooms.size(), roomTypeId, arrivalDate, departureDate);
            return ResponseEntity.ok(ApiResponse.success(message, availableRooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get available rooms: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{reservationNo}")
    @Operation(summary = "Delete reservation", description = "Delete a reservation by its number")
    public ResponseEntity<ApiResponse<String>> deleteReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            reservationService.deleteReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to delete reservation: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{reservationNo}/rooms-checked-in")
    @Operation(summary = "Update rooms checked-in count", description = "Update the number of rooms checked-in for a reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateRoomsCheckedIn(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo,
            @Parameter(description = "Rooms checked-in count") @RequestBody Map<String, Integer> request) {
        try {
            Integer roomsCheckedIn = request.get("roomsCheckedIn");
            if (roomsCheckedIn == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("roomsCheckedIn field is required"));
            }
            
            ReservationResponse response = reservationService.updateRoomsCheckedIn(reservationNo, roomsCheckedIn);
            return ResponseEntity.ok(ApiResponse.success("Rooms checked-in count updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update rooms checked-in count: " + e.getMessage()));
        }
    }
}