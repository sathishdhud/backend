package com.hotelworks.controller;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.request.SalesReceiptRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.dto.response.SalesReceiptResponse;
import com.hotelworks.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing expenses and sales receipts")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/expenses")
    @Operation(summary = "Create expense", description = "Create a new expense transaction")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = transactionService.createExpense(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create expense: " + e.getMessage()));
        }
    }
    
    @PostMapping("/sales-receipts")
    @Operation(summary = "Create sales receipt", description = "Create a new sales receipt")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<SalesReceiptResponse>> createSalesReceipt(
            @Valid @RequestBody SalesReceiptRequest request) {
        try {
            SalesReceiptResponse response = transactionService.createSalesReceipt(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create sales receipt: " + e.getMessage()));
        }
    }
    
    @GetMapping("/expenses")
    @Operation(summary = "Get all expenses", description = "Retrieve all expense transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses() {
        try {
            List<ExpenseResponse> expenses = transactionService.getAllExpenses();
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sales-receipts")
    @Operation(summary = "Get all sales receipts", description = "Retrieve all sales receipts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SalesReceiptResponse>>> getAllSalesReceipts() {
        try {
            List<SalesReceiptResponse> receipts = transactionService.getAllSalesReceipts();
            return ResponseEntity.ok(ApiResponse.success(receipts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve sales receipts: " + e.getMessage()));
        }
    }
}