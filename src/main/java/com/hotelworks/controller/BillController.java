package com.hotelworks.controller;

import com.hotelworks.dto.request.SplitBillRequest;
import com.hotelworks.dto.request.BillSettlementRequest;
import com.hotelworks.dto.request.BillUpdateRequest;
import com.hotelworks.dto.response.*;
import com.hotelworks.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Bills", description = "Hotel Bill Management APIs")
public class BillController {
    
    @Autowired
    private BillService billService;
    
    @PostMapping("/generate/{folioNo}")
    @Operation(summary = "Generate bill", description = "Generate bill for checkout from folio number")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillResponse>> generateBill(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            BillResponse response = billService.generateBill(folioNo);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill generated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to generate bill: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{billNo}")
    @Operation(summary = "Get bill by number", description = "Retrieve bill details by bill number")
    public ResponseEntity<ApiResponse<BillResponse>> getBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            BillResponse response = billService.getBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Bill not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{billNo}")
    @Operation(summary = "Update bill", description = "Update bill information by bill number")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillResponse>> updateBill(
            @Parameter(description = "Bill number") @PathVariable String billNo,
            @Valid @RequestBody BillUpdateRequest request) {
        try {
            BillResponse response = billService.updateBill(billNo, request);
            return ResponseEntity.ok(ApiResponse.success("Bill updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update bill: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}")
    @Operation(summary = "Get bill by folio", description = "Retrieve bill details by folio number")
    public ResponseEntity<ApiResponse<BillResponse>> getBillByFolio(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            BillResponse response = billService.getBillByFolio(folioNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Bill not found for folio: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search bills", description = "Search bills by guest name, bill number, or folio number")
    public ResponseEntity<ApiResponse<List<BillResponse>>> searchBills(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        try {
            List<BillResponse> responses = billService.searchBills(searchTerm);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{billNo}/split/preview")
    @Operation(summary = "Get split bill preview", description = "Get bill details with transactions for split bill selection")
    public ResponseEntity<ApiResponse<SplitBillPreviewResponse>> getSplitBillPreview(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            SplitBillPreviewResponse response = billService.getSplitBillPreview(billNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Failed to get split bill preview: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{billNo}/split")
    @Operation(summary = "Execute split bill", description = "Split bill by moving selected transactions to a new bill")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SplitBillResponse>> executeSplitBill(
            @Parameter(description = "Original bill number") @PathVariable String billNo,
            @Valid @RequestBody SplitBillRequest request) {
        try {
            // Ensure the path variable matches the request
            request.setOriginalBillNo(billNo);
            
            SplitBillResponse response = billService.splitBill(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill split successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to split bill: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{billNo}/related")
    @Operation(summary = "Get related bills", description = "Get original bill and all its split bills")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getRelatedBills(
            @Parameter(description = "Bill number (original or split)") @PathVariable String billNo) {
        try {
            List<BillResponse> responses = billService.getRelatedBills(billNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Failed to get related bills: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{billNo}/payment")
    @Operation(summary = "Process payment for bill", description = "Process payment against a bill number for deferred payment support")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillSettlementResponse>> processPayment(
            @Parameter(description = "Bill number") @PathVariable String billNo,
            @Valid @RequestBody BillSettlementRequest request) {
        try {
            // Ensure bill number in request matches path parameter
            request.setBillNo(billNo);
            
            BillSettlementResponse response = billService.processPayment(request);
            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Payment processing failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{billNo}/settlement-status")
    @Operation(summary = "Get bill settlement status", description = "Get current settlement status and payment details for a bill")
    public ResponseEntity<ApiResponse<BillSettlementResponse>> getBillSettlementStatus(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            BillSettlementResponse response = billService.getBillSettlementStatus(billNo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Bill settlement status not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/pending-settlements")
    @Operation(summary = "Get pending bill settlements", description = "Get all bills with pending or partial payments")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BillSettlementResponse>>> getPendingBillSettlements() {
        try {
            List<BillSettlementResponse> responses = billService.getPendingBills();
            return ResponseEntity.ok(ApiResponse.success("Pending settlements retrieved successfully", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving pending settlements: " + e.getMessage()));
        }
    }
}