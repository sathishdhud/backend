package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.Expense;
import com.hotelworks.entity.PostTransaction; // Added import
import com.hotelworks.entity.Room; // Added import
import com.hotelworks.entity.FoBill; // Added import
import com.hotelworks.entity.CheckIn; // Added import
import com.hotelworks.repository.ExpenseRepository;
import com.hotelworks.repository.PostTransactionRepository; // Added import
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository; // Added import
import com.hotelworks.repository.FoBillRepository; // Added import
import com.hotelworks.repository.CheckInRepository; // Added import
import com.hotelworks.repository.HmsystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Added import
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    @Autowired
    private PostTransactionRepository postTransactionRepository; // Changed from ExpenseRepository
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private RoomRepository roomRepository; // Added
    
    @Autowired
    private FoBillRepository foBillRepository; // Added
    
    @Autowired
    private CheckInRepository checkInRepository; // Added
    
    @Autowired
    private HmsystemRepository hmsystemRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Get the current audit date from the HMS system
     */
    private LocalDate getCurrentAuditDate() {
        Optional<com.hotelworks.entity.Hmsystem> latestHmsystemOpt = hmsystemRepository.findLatestRecord();
        if (latestHmsystemOpt.isPresent()) {
            return latestHmsystemOpt.get().getShiftDate();
        } else {
            // Fallback to current system date if no HMS record exists
            return LocalDate.now();
        }
    }
    
    /**
     * Create a new expense
     */
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        try {
            // Create PostTransaction entity instead of Expense
            PostTransaction transaction = new PostTransaction();
            transaction.setTransactionId(numberGenerationService.generateTransactionId()); // Changed from generateExpenseId()
            // Generate proper sequential voucher number instead of using request body
            transaction.setVoucherNo(numberGenerationService.generateExpenseVoucherNumber());
            transaction.setDate(request.getDate());
            transaction.setAccHeadId(request.getAccountHeadId()); // Changed from setAccountHeadId()
            transaction.setAmount(request.getAmount());
            transaction.setNarration(request.getNarration());
            transaction.setShiftNo(request.getShiftNo());
            transaction.setShiftDate(request.getShiftDate());
            transaction.setAuditDate(getCurrentAuditDate()); // Use audit date from HMS system
            
            // Validate account head exists
            if (!hotelAccountHeadRepository.existsById(request.getAccountHeadId())) {
                throw new RuntimeException("Account head not found: " + request.getAccountHeadId());
            }
            
            // Set room, bill, folio, and guest information if provided
            if (request.getRoomNo() != null && !request.getRoomNo().isEmpty()) {
                Room room = roomRepository.findByRoomNo(request.getRoomNo())
                    .orElseThrow(() -> new RuntimeException("Room not found: " + request.getRoomNo()));
                transaction.setRoomId(room.getRoomId());
            }
            
            if (request.getBillNo() != null && !request.getBillNo().isEmpty()) {
                if (!foBillRepository.existsById(request.getBillNo())) {
                    throw new RuntimeException("Bill not found: " + request.getBillNo());
                }
                transaction.setBillNo(request.getBillNo());
            }
            
            if (request.getFolioNo() != null && !request.getFolioNo().isEmpty()) {
                if (!checkInRepository.existsById(request.getFolioNo())) {
                    throw new RuntimeException("Folio not found: " + request.getFolioNo());
                }
                transaction.setFolioNo(request.getFolioNo());
            }
            
            if (request.getGuestName() != null && !request.getGuestName().isEmpty()) {
                transaction.setGuestName(request.getGuestName());
            } else if (request.getBillNo() != null && !request.getBillNo().isEmpty()) {
                // Try to get guest name from bill
                FoBill bill = foBillRepository.findById(request.getBillNo()).orElse(null);
                if (bill != null && bill.getGuestName() != null) {
                    transaction.setGuestName(bill.getGuestName());
                }
            } else if (request.getFolioNo() != null && !request.getFolioNo().isEmpty()) {
                // Try to get guest name from check-in
                CheckIn checkIn = checkInRepository.findById(request.getFolioNo()).orElse(null);
                if (checkIn != null && checkIn.getGuestName() != null) {
                    transaction.setGuestName(checkIn.getGuestName());
                }
            }
            
            // Ensure guest name is set
            if (transaction.getGuestName() == null || transaction.getGuestName().isEmpty()) {
                transaction.setGuestName("Unknown Guest");
            }
            
            // Save the transaction
            PostTransaction savedTransaction = postTransactionRepository.save(transaction);
            
            // Convert to response DTO
            return convertToExpenseResponse(savedTransaction);
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
    public ExpenseResponse updateExpense(String transactionId, ExpenseRequest request) { // Changed parameter from expenseId to transactionId
        try {
            // Find the existing transaction
            PostTransaction transaction = postTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Expense not found with transaction ID: " + transactionId)); // Changed message
            
            // Validate account head exists
            if (!hotelAccountHeadRepository.existsById(request.getAccountHeadId())) {
                throw new RuntimeException("Account head not found: " + request.getAccountHeadId());
            }
            
            // Update the transaction fields
            // Note: voucher number is not updated as it's a sequential identifier
            transaction.setDate(request.getDate());
            transaction.setAccHeadId(request.getAccountHeadId()); // Changed from setAccountHeadId()
            transaction.setAmount(request.getAmount());
            transaction.setNarration(request.getNarration());
            transaction.setShiftNo(request.getShiftNo());
            transaction.setShiftDate(request.getShiftDate());
            transaction.setAuditDate(getCurrentAuditDate()); // Update audit date to current date
            
            // Update room, bill, folio, and guest information if provided
            if (request.getRoomNo() != null && !request.getRoomNo().isEmpty()) {
                Room room = roomRepository.findByRoomNo(request.getRoomNo())
                    .orElseThrow(() -> new RuntimeException("Room not found: " + request.getRoomNo()));
                transaction.setRoomId(room.getRoomId());
            } else {
                transaction.setRoomId(null);
            }
            
            if (request.getBillNo() != null && !request.getBillNo().isEmpty()) {
                if (!foBillRepository.existsById(request.getBillNo())) {
                    throw new RuntimeException("Bill not found: " + request.getBillNo());
                }
                transaction.setBillNo(request.getBillNo());
            } else {
                transaction.setBillNo(null);
            }
            
            if (request.getFolioNo() != null && !request.getFolioNo().isEmpty()) {
                if (!checkInRepository.existsById(request.getFolioNo())) {
                    throw new RuntimeException("Folio not found: " + request.getFolioNo());
                }
                transaction.setFolioNo(request.getFolioNo());
            } else {
                transaction.setFolioNo(null);
            }
            
            if (request.getGuestName() != null && !request.getGuestName().isEmpty()) {
                transaction.setGuestName(request.getGuestName());
            } else if (request.getBillNo() != null && !request.getBillNo().isEmpty()) {
                // Try to get guest name from bill
                FoBill bill = foBillRepository.findById(request.getBillNo()).orElse(null);
                if (bill != null && bill.getGuestName() != null) {
                    transaction.setGuestName(bill.getGuestName());
                }
            } else if (request.getFolioNo() != null && !request.getFolioNo().isEmpty()) {
                // Try to get guest name from check-in
                CheckIn checkIn = checkInRepository.findById(request.getFolioNo()).orElse(null);
                if (checkIn != null && checkIn.getGuestName() != null) {
                    transaction.setGuestName(checkIn.getGuestName());
                }
            }
            
            // Ensure guest name is set
            if (transaction.getGuestName() == null || transaction.getGuestName().isEmpty()) {
                transaction.setGuestName("Unknown Guest");
            }
            
            // Save the updated transaction
            PostTransaction savedTransaction = postTransactionRepository.save(transaction);
            
            // Convert to response DTO
            return convertToExpenseResponse(savedTransaction);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to update expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete an expense by transaction ID
     */
    @Transactional
    public void deleteExpense(String transactionId) { // Changed parameter from expenseId to transactionId
        try {
            // Check if the expense exists
            if (!postTransactionRepository.existsById(transactionId)) { // Changed from expenseRepository
                throw new RuntimeException("Expense not found with transaction ID: " + transactionId); // Changed message
            }
            
            // Delete the expense
            postTransactionRepository.deleteById(transactionId); // Changed from expenseRepository
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to delete expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get expense by transaction ID
     */
    public ExpenseResponse getExpenseById(String transactionId) { // Changed method name and parameter
        PostTransaction transaction = postTransactionRepository.findById(transactionId) // Changed from expenseRepository
            .orElseThrow(() -> new RuntimeException("Expense not found with transaction ID: " + transactionId)); // Changed message
        return convertToExpenseResponse(transaction);
    }
    
    /**
     * Get all expenses
     */
    public List<ExpenseResponse> getAllExpenses() {
        List<PostTransaction> transactions = postTransactionRepository.findAll(); // Changed from expenseRepository
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by voucher number
     */
    public List<ExpenseResponse> getExpensesByVoucherNo(String voucherNo) {
        // Need to query PostTransaction by voucherNo
        List<PostTransaction> transactions = postTransactionRepository.findByVoucherNo(voucherNo); // Changed implementation
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by account head
     */
    public List<ExpenseResponse> getExpensesByAccountHead(String accountHeadId) {
        // Need to query PostTransaction by account head ID
        List<PostTransaction> transactions = postTransactionRepository.findByAccHeadId(accountHeadId); // Changed implementation and method call
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert PostTransaction entity to ExpenseResponse DTO
     */
    private ExpenseResponse convertToExpenseResponse(PostTransaction transaction) { // Changed parameter type
        ExpenseResponse response = new ExpenseResponse();
        response.setTransactionId(transaction.getTransactionId()); // Changed from getExpenseId()
        response.setVoucherNo(transaction.getVoucherNo());
        response.setDate(transaction.getDate());
        response.setAccountHeadId(transaction.getAccHeadId()); // Changed from getAccountHeadId()
        response.setAmount(transaction.getAmount());
        response.setNarration(transaction.getNarration());
        response.setShiftNo(transaction.getShiftNo());
        response.setShiftDate(transaction.getShiftDate());
        
        // Additional fields
        response.setFolioNo(transaction.getFolioNo());
        response.setBillNo(transaction.getBillNo());
        response.setRoomId(transaction.getRoomId());
        response.setGuestName(transaction.getGuestName());
        response.setAuditDate(transaction.getAuditDate());
        
        // Try to get account head name
        if (transaction.getAccHeadId() != null) { // Changed from getAccountHeadId()
            hotelAccountHeadRepository.findById(transaction.getAccHeadId()).ifPresent(accountHead -> { // Changed from getAccountHeadId()
                response.setAccountHeadName(accountHead.getName());
            });
        }
        
        // Try to get room number
        if (transaction.getRoomId() != null) {
            roomRepository.findById(transaction.getRoomId()).ifPresent(room -> {
                response.setRoomNo(room.getRoomNo());
            });
        }
        
        return response;
    }
}