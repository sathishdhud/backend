package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.Expense;
import com.hotelworks.repository.ExpenseRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new expense
     */
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        try {
            // Create Expense entity
            Expense expense = new Expense();
            expense.setExpenseId(numberGenerationService.generateExpenseId());
            expense.setVoucherNo(request.getVoucherNo());
            expense.setDate(request.getDate());
            expense.setAccountHeadId(request.getAccountHeadId());
            expense.setAmount(request.getAmount());
            expense.setNarration(request.getNarration());
            expense.setShiftNo(request.getShiftNo());
            expense.setShiftDate(request.getShiftDate());
            
            // Validate account head exists
            if (!hotelAccountHeadRepository.existsById(request.getAccountHeadId())) {
                throw new RuntimeException("Account head not found: " + request.getAccountHeadId());
            }
            
            // Save the expense
            Expense savedExpense = expenseRepository.save(expense);
            
            // Convert to response DTO
            return convertToExpenseResponse(savedExpense);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to create expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an existing expense
     */
    @Transactional
    public ExpenseResponse updateExpense(String expenseId, ExpenseRequest request) {
        try {
            // Find the existing expense
            Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
            
            // Validate account head exists
            if (!hotelAccountHeadRepository.existsById(request.getAccountHeadId())) {
                throw new RuntimeException("Account head not found: " + request.getAccountHeadId());
            }
            
            // Update the expense fields
            expense.setVoucherNo(request.getVoucherNo());
            expense.setDate(request.getDate());
            expense.setAccountHeadId(request.getAccountHeadId());
            expense.setAmount(request.getAmount());
            expense.setNarration(request.getNarration());
            expense.setShiftNo(request.getShiftNo());
            expense.setShiftDate(request.getShiftDate());
            
            // Save the updated expense
            Expense savedExpense = expenseRepository.save(expense);
            
            // Convert to response DTO
            return convertToExpenseResponse(savedExpense);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to update expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete an expense by ID
     */
    @Transactional
    public void deleteExpense(String expenseId) {
        try {
            // Check if the expense exists
            if (!expenseRepository.existsById(expenseId)) {
                throw new RuntimeException("Expense not found with ID: " + expenseId);
            }
            
            // Delete the expense
            expenseRepository.deleteById(expenseId);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to delete expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get expense by ID
     */
    public ExpenseResponse getExpenseById(String expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
        return convertToExpenseResponse(expense);
    }
    
    /**
     * Get all expenses
     */
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by voucher number
     */
    public List<ExpenseResponse> getExpensesByVoucherNo(String voucherNo) {
        List<Expense> expenses = expenseRepository.findByVoucherNo(voucherNo);
        return expenses.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by account head
     */
    public List<ExpenseResponse> getExpensesByAccountHead(String accountHeadId) {
        List<Expense> expenses = expenseRepository.findByAccountHeadId(accountHeadId);
        return expenses.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Expense entity to ExpenseResponse DTO
     */
    private ExpenseResponse convertToExpenseResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setTransactionId(expense.getExpenseId());
        response.setVoucherNo(expense.getVoucherNo());
        response.setDate(expense.getDate());
        response.setAccountHeadId(expense.getAccountHeadId());
        
        // Try to get account head name
        if (expense.getAccountHeadId() != null) {
            hotelAccountHeadRepository.findById(expense.getAccountHeadId()).ifPresent(accountHead -> 
                response.setAccountHeadName(accountHead.getName())
            );
        }
        
        response.setAmount(expense.getAmount());
        response.setNarration(expense.getNarration());
        response.setShiftNo(expense.getShiftNo());
        response.setShiftDate(expense.getShiftDate());
        
        return response;
    }
}