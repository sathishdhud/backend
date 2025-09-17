package com.hotelworks.controller;

import com.hotelworks.dto.request.AdvanceRequest;
import com.hotelworks.dto.response.AdvanceResponse;
import com.hotelworks.dto.response.AdvanceSummaryResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.AdvanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/advances")
@Tag(name = "Advances", description = "Hotel Advance Payment Management APIs")
public class AdvanceController {
    
    @Autowired
    private AdvanceService advanceService;
    
    @PostMapping("/reservation")
    @Operation(summary = "Create advance for reservation", description = "Create advance payment for a reservation")
    public ResponseEntity<ApiResponse<AdvanceResponse>> createAdvanceForReservation(
            @Valid @RequestBody AdvanceRequest request) {
        try {
            AdvanceResponse response = advanceService.createAdvanceForReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully for reservation", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create advance: " + e.getMessage()));
        }
    }
    
    @PostMapping("/inhouse")
    @Operation(summary = "Create advance for in-house guest", description = "Create advance payment for an in-house guest")
    public ResponseEntity<ApiResponse<AdvanceResponse>> createAdvanceForInHouseGuest(
            @Valid @RequestBody AdvanceRequest request) {
        try {
            AdvanceResponse response = advanceService.createAdvanceForInHouseGuest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully for in-house guest", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create advance: " + e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    @Operation(summary = "Create advance for checkout guest", description = "Create advance payment for a checkout guest")
    public ResponseEntity<ApiResponse<AdvanceResponse>> createAdvanceForCheckoutGuest(
            @Valid @RequestBody AdvanceRequest request) {
        try {
            AdvanceResponse response = advanceService.createAdvanceForCheckoutGuest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully for checkout guest", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create advance: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reservation/{reservationNo}")
    @Operation(summary = "Get advances by reservation", description = "Get all advances for a reservation")
    public ResponseEntity<ApiResponse<List<AdvanceResponse>>> getAdvancesByReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            List<AdvanceResponse> responses = advanceService.getAdvancesByReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}")
    @Operation(summary = "Get advances by folio", description = "Get all advances for a folio")
    public ResponseEntity<ApiResponse<List<AdvanceResponse>>> getAdvancesByFolio(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            List<AdvanceResponse> responses = advanceService.getAdvancesByFolio(folioNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}")
    @Operation(summary = "Get advances by bill", description = "Get all advances for a bill")
    public ResponseEntity<ApiResponse<List<AdvanceResponse>>> getAdvancesByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            List<AdvanceResponse> responses = advanceService.getAdvancesByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reservation/{reservationNo}/total")
    @Operation(summary = "Get total advances by reservation", description = "Get total advance amount for a reservation")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAdvancesByReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            BigDecimal total = advanceService.getTotalAdvancesByReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get total advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}/total")
    @Operation(summary = "Get total advances by folio", description = "Get total advance amount for a folio")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAdvancesByFolio(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            BigDecimal total = advanceService.getTotalAdvancesByFolio(folioNo);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get total advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}/total")
    @Operation(summary = "Get total advances by bill", description = "Get total advance amount for a bill")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAdvancesByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            BigDecimal total = advanceService.getTotalAdvancesByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get total advances: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reservation/{reservationNo}/summary")
    @Operation(summary = "Get advance summary by reservation", description = "Get detailed advance summary for a reservation")
    public ResponseEntity<ApiResponse<AdvanceSummaryResponse>> getAdvanceSummaryByReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            AdvanceSummaryResponse summary = advanceService.getAdvanceSummaryByReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get advance summary: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}/summary")
    @Operation(summary = "Get advance summary by bill", description = "Get detailed advance summary for a bill")
    public ResponseEntity<ApiResponse<AdvanceSummaryResponse>> getAdvanceSummaryByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            AdvanceSummaryResponse summary = advanceService.getAdvanceSummaryByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get advance summary: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{advanceId}")
    @Operation(summary = "Get advance by ID", description = "Get advance details by advance ID")
    public ResponseEntity<ApiResponse<AdvanceResponse>> getAdvance(
            @Parameter(description = "Advance ID") @PathVariable String advanceId) {
        try {
            AdvanceResponse response = advanceService.getAdvance(advanceId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Advance not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{advanceId}")
    @Operation(summary = "Update advance", description = "Update advance payment details")
    public ResponseEntity<ApiResponse<AdvanceResponse>> updateAdvance(
            @Parameter(description = "Advance ID") @PathVariable String advanceId,
            @Valid @RequestBody AdvanceRequest request) {
        try {
            AdvanceResponse response = advanceService.updateAdvance(advanceId, request);
            return ResponseEntity.ok(ApiResponse.success("Advance updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update advance: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{advanceId}")
    @Operation(summary = "Delete advance", description = "Delete advance payment by ID")
    public ResponseEntity<ApiResponse<String>> deleteAdvance(
            @Parameter(description = "Advance ID") @PathVariable String advanceId) {
        try {
            advanceService.deleteAdvance(advanceId);
            return ResponseEntity.ok(ApiResponse.success("Advance deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete advance: " + e.getMessage()));
        }
    }

    @GetMapping("/bill/{billNo}/guest-name")
    @Operation(summary = "Get guest name by bill", description = "Get guest name for a bill")
    public ResponseEntity<ApiResponse<String>> getGuestNameByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            String guestName = advanceService.getGuestNameByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(guestName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get guest name: " + e.getMessage()));
        }
    }

    @GetMapping("/reservation/{reservationNo}/guest-name")
    @Operation(summary = "Get guest name by reservation", description = "Get guest name for a reservation")
    public ResponseEntity<ApiResponse<String>> getGuestNameByReservation(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo) {
        try {
            String guestName = advanceService.getGuestNameByReservation(reservationNo);
            return ResponseEntity.ok(ApiResponse.success(guestName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get guest name: " + e.getMessage()));
        }
    }
    
    // New endpoint for creating advance by reservation and bill number
    @PostMapping("/reservation/{reservationNo}/bill/{billNo}")
    @Operation(summary = "Create advance for reservation with bill", description = "Create advance payment for a reservation associated with a specific bill")
    public ResponseEntity<ApiResponse<AdvanceResponse>> createAdvanceForReservationWithBill(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo,
            @Parameter(description = "Bill number") @PathVariable String billNo,
            @Valid @RequestBody AdvanceRequest request) {
        try {
            // Set the reservation and bill numbers in the request
            request.setReservationNo(reservationNo);
            request.setBillNo(billNo);
            
            AdvanceResponse response = advanceService.createAdvanceForReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully for reservation with bill", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create advance: " + e.getMessage()));
        }
    }
    
    // New endpoint for creating advance by reservation and folio number
    @PostMapping("/reservation/{reservationNo}/folio/{folioNo}")
    @Operation(summary = "Create advance for reservation with folio", description = "Create advance payment for a reservation associated with a specific folio")
    public ResponseEntity<ApiResponse<AdvanceResponse>> createAdvanceForReservationWithFolio(
            @Parameter(description = "Reservation number") @PathVariable String reservationNo,
            @Parameter(description = "Folio number") @PathVariable String folioNo,
            @Valid @RequestBody AdvanceRequest request) {
        try {
            // Set the reservation and folio numbers in the request
            request.setReservationNo(reservationNo);
            request.setFolioNo(folioNo);
            
            AdvanceResponse response = advanceService.createAdvanceForReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully for reservation with folio", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create advance: " + e.getMessage()));
        }
    }
}