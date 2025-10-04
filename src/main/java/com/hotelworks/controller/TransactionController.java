package com.hotelworks.controller;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.request.SalesReceiptRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.dto.response.SalesReceiptResponse;
import com.hotelworks.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create expense: " + e.getMessage()));
        }
    }
    
    @GetMapping("/expenses/{transactionId}")
    @Operation(summary = "Get expense by transaction ID", description = "Retrieve an expense transaction by its transaction ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseByTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        try {
            ExpenseResponse expense = transactionService.getExpenseByTransactionId(transactionId);
            return ResponseEntity.ok(ApiResponse.success(expense));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expense: " + e.getMessage()));
        }
    }
    
    @PutMapping("/expenses/{transactionId}")
    @Operation(summary = "Update expense", description = "Update an existing expense transaction")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId,
            @Valid @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = transactionService.updateExpense(transactionId, request);
            return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update expense: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/expenses/{transactionId}")
    @Operation(summary = "Delete expense", description = "Delete an expense transaction by its transaction ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> deleteExpense(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        try {
            transactionService.deleteExpense(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete expense: " + e.getMessage()));
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
            e.printStackTrace(); // Log the full stack trace for debugging
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
    
    @GetMapping("/expenses/room/{roomNo}")
    @Operation(summary = "Get expenses by room number", description = "Retrieve all expense transactions for a specific room")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByRoomNo(
            @Parameter(description = "Room number") @PathVariable String roomNo) {
        try {
            List<ExpenseResponse> expenses = transactionService.getExpensesByRoomNo(roomNo);
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/expenses/bill/{billNo}")
    @Operation(summary = "Get expenses by bill number", description = "Retrieve all expense transactions for a specific bill")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByBillNo(
            @Parameter(description = "Bill number") @PathVariable String billNo) {
        try {
            List<ExpenseResponse> expenses = transactionService.getExpensesByBillNo(billNo);
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/expenses/folio/{folioNo}")
    @Operation(summary = "Get expenses by folio number", description = "Retrieve all expense transactions for a specific folio")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByFolioNo(
            @Parameter(description = "Folio number") @PathVariable String folioNo) {
        try {
            List<ExpenseResponse> expenses = transactionService.getExpensesByFolioNo(folioNo);
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @PostMapping("/expenses/bill/{billNo}")
    @Operation(summary = "Create expense for bill", description = "Create a new expense transaction for a specific bill")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpenseForBill(
            @Parameter(description = "Bill number") @PathVariable String billNo,
            @Valid @RequestBody ExpenseRequest request) {
        try {
            // Set the bill number in the request
            request.setBillNo(billNo);
            
            ExpenseResponse response = transactionService.createExpense(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create expense: " + e.getMessage()));
        }
    }
    
    @PostMapping("/sales-receipts/bill/{billNo}")
    @Operation(summary = "Create sales receipt for bill", description = "Create a new sales receipt for a specific bill")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<SalesReceiptResponse>> createSalesReceiptForBill(
            @Parameter(description = "Bill number") @PathVariable String billNo,
            @Valid @RequestBody SalesReceiptRequest request) {
        try {
            // Set the bill number in the request
            request.setBillNo(billNo);
            
            SalesReceiptResponse response = transactionService.createSalesReceipt(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create sales receipt: " + e.getMessage()));
        }
    }
}