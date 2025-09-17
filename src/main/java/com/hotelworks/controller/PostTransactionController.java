package com.hotelworks.controller;

import com.hotelworks.dto.request.PostTransactionRequest;
import com.hotelworks.dto.response.PostTransactionResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.PostTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Post Transactions", description = "Hotel Post Transaction Management APIs")
public class PostTransactionController {
    
    @Autowired
    private PostTransactionService postTransactionService;
    
    @PostMapping("/inhouse")
    @Operation(summary = "Create transaction for in-house guest", description = "Create post transaction for an in-house guest")
    public ResponseEntity<ApiResponse<PostTransactionResponse>> createTransactionForInHouseGuest(
            @Valid @RequestBody PostTransactionRequest request) {
        try {
            PostTransactionResponse response = postTransactionService.createTransactionForInHouseGuest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully for in-house guest", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create transaction: " + e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    @Operation(summary = "Create transaction for checkout guest", description = "Create post transaction for a checkout guest")
    public ResponseEntity<ApiResponse<PostTransactionResponse>> createTransactionForCheckoutGuest(
            @Valid @RequestBody PostTransactionRequest request) {
        try {
            PostTransactionResponse response = postTransactionService.createTransactionForCheckoutGuest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully for checkout guest", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create transaction: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}")
    @Operation(summary = "Get transactions by folio", description = "Get all transactions for a folio")
    public ResponseEntity<ApiResponse<List<PostTransactionResponse>>> getTransactionsByFolio(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            List<PostTransactionResponse> responses = postTransactionService.getTransactionsByFolio(folioNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}")
    @Operation(summary = "Get transactions by bill", description = "Get all transactions for a bill")
    public ResponseEntity<ApiResponse<List<PostTransactionResponse>>> getTransactionsByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            List<PostTransactionResponse> responses = postTransactionService.getTransactionsByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get transactions by room", description = "Get all transactions for a room")
    public ResponseEntity<ApiResponse<List<PostTransactionResponse>>> getTransactionsByRoom(
            @Parameter(description = "Room ID") @PathVariable String roomId) {
        try {
            List<PostTransactionResponse> responses = postTransactionService.getTransactionsByRoom(roomId);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/folio/{folioNo}/total")
    @Operation(summary = "Get total transactions by folio", description = "Get total transaction amount for a folio")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalTransactionsByFolio(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            BigDecimal total = postTransactionService.getTotalTransactionsByFolio(folioNo);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get total transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{billNo}/total")
    @Operation(summary = "Get total transactions by bill", description = "Get total transaction amount for a bill")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalTransactionsByBill(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            BigDecimal total = postTransactionService.getTotalTransactionsByBill(billNo);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get total transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", description = "Get transactions between two dates")
    public ResponseEntity<ApiResponse<List<PostTransactionResponse>>> getTransactionsBetweenDates(
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<PostTransactionResponse> responses = postTransactionService.getTransactionsBetweenDates(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to get transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Get transaction details by transaction ID")
    public ResponseEntity<ApiResponse<PostTransactionResponse>> getTransaction(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        try {
            PostTransactionResponse response = postTransactionService.getTransaction(transactionId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Transaction not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{transactionId}")
    @Operation(summary = "Update transaction", description = "Update post transaction details")
    public ResponseEntity<ApiResponse<PostTransactionResponse>> updateTransaction(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId,
            @Valid @RequestBody PostTransactionRequest request) {
        try {
            PostTransactionResponse response = postTransactionService.updateTransaction(transactionId, request);
            return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update transaction: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Delete post transaction by ID")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        try {
            postTransactionService.deleteTransaction(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete transaction: " + e.getMessage()));
        }
    }
}