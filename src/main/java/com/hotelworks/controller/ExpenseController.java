package com.hotelworks.controller;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.service.ExpenseService;
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
@RequestMapping("/api/expenses")
@Tag(name = "Expense Management", description = "APIs for managing hotel expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @PostMapping
    @Operation(summary = "Create expense", description = "Create a new hotel expense")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = expenseService.createExpense(request);
            return ResponseEntity.ok(ApiResponse.success("Expense created successfully", response));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create expense: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense by ID", description = "Retrieve an expense by its ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @Parameter(description = "Expense ID") @PathVariable String expenseId) {
        try {
            ExpenseResponse expense = expenseService.getExpenseById(expenseId);
            return ResponseEntity.ok(ApiResponse.success(expense));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expense: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{expenseId}")
    @Operation(summary = "Update expense", description = "Update an existing expense")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @Parameter(description = "Expense ID") @PathVariable String expenseId,
            @Valid @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = expenseService.updateExpense(expenseId, request);
            return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update expense: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete expense", description = "Delete an expense by its ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> deleteExpense(
            @Parameter(description = "Expense ID") @PathVariable String expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete expense: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all expenses", description = "Retrieve all expenses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses() {
        try {
            List<ExpenseResponse> expenses = expenseService.getAllExpenses();
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/voucher/{voucherNo}")
    @Operation(summary = "Get expenses by voucher number", description = "Retrieve expenses by voucher number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByVoucherNo(
            @Parameter(description = "Voucher number") @PathVariable String voucherNo) {
        try {
            List<ExpenseResponse> expenses = expenseService.getExpensesByVoucherNo(voucherNo);
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/account-head/{accountHeadId}")
    @Operation(summary = "Get expenses by account head", description = "Retrieve expenses by account head ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByAccountHead(
            @Parameter(description = "Account head ID") @PathVariable String accountHeadId) {
        try {
            List<ExpenseResponse> expenses = expenseService.getExpensesByAccountHead(accountHeadId);
            return ResponseEntity.ok(ApiResponse.success(expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve expenses: " + e.getMessage()));
        }
    }
}