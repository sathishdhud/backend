package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.request.SalesReceiptRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.dto.response.SalesReceiptResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.SalesReceipt;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.SalesReceiptRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.FoBillRepository;
import com.hotelworks.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private SalesReceiptRepository salesReceiptRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private FoBillRepository foBillRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new expense transaction
     */
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        try {
            // Create PostTransaction entity
            PostTransaction transaction = new PostTransaction();
            transaction.setTransactionId(numberGenerationService.generateTransactionId());
            transaction.setVoucherNo(request.getVoucherNo());
            transaction.setDate(request.getDate());
            transaction.setAccHeadId(request.getAccountHeadId());
            transaction.setAmount(request.getAmount());
            transaction.setNarration(request.getNarration());
            transaction.setShiftNo(request.getShiftNo());
            transaction.setShiftDate(request.getShiftDate());
            
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
     * Update an existing expense transaction
     */
    @Transactional
    public ExpenseResponse updateExpense(String transactionId, ExpenseRequest request) {
        try {
            // Find the existing transaction
            PostTransaction transaction = postTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Expense not found with transaction ID: " + transactionId));
            
            // Validate account head exists
            if (!hotelAccountHeadRepository.existsById(request.getAccountHeadId())) {
                throw new RuntimeException("Account head not found: " + request.getAccountHeadId());
            }
            
            // Update the transaction fields
            transaction.setVoucherNo(request.getVoucherNo());
            transaction.setDate(request.getDate());
            transaction.setAccHeadId(request.getAccountHeadId());
            transaction.setAmount(request.getAmount());
            transaction.setNarration(request.getNarration());
            transaction.setShiftNo(request.getShiftNo());
            transaction.setShiftDate(request.getShiftDate());
            
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
     * Delete an expense transaction by transaction ID
     */
    @Transactional
    public void deleteExpense(String transactionId) {
        try {
            // Check if the expense exists
            if (!postTransactionRepository.existsById(transactionId)) {
                throw new RuntimeException("Expense not found with transaction ID: " + transactionId);
            }
            
            // Delete the expense
            postTransactionRepository.deleteById(transactionId);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to delete expense: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get expense by transaction ID
     */
    public ExpenseResponse getExpenseByTransactionId(String transactionId) {
        PostTransaction transaction = postTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Expense not found with transaction ID: " + transactionId));
        return convertToExpenseResponse(transaction);
    }
    
    /**
     * Create a new sales receipt
     */
    @Transactional
    public SalesReceiptResponse createSalesReceipt(SalesReceiptRequest request) {
        try {
            // Create SalesReceipt entity
            SalesReceipt receipt = new SalesReceipt();
            receipt.setReceiptNo(request.getReceiptNo());
            receipt.setDate(request.getDate());
            receipt.setModeOfPaymentId(request.getModeOfPaymentId());
            receipt.setAmount(request.getAmount());
            receipt.setVoucherNo(request.getVoucherNo());
            receipt.setNarration(request.getNarration());
            receipt.setShiftNo(request.getShiftNo());
            receipt.setShiftDate(request.getShiftDate());
            
            // Save the receipt
            SalesReceipt savedReceipt = salesReceiptRepository.save(receipt);
            
            // Convert to response DTO
            return convertToSalesReceiptResponse(savedReceipt);
        } catch (Exception e) {
            // Log the actual exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Failed to create sales receipt: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all expenses
     */
    public List<ExpenseResponse> getAllExpenses() {
        List<PostTransaction> transactions = postTransactionRepository.findAll();
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all sales receipts
     */
    public List<SalesReceiptResponse> getAllSalesReceipts() {
        List<SalesReceipt> receipts = salesReceiptRepository.findAll();
        return receipts.stream()
                .map(this::convertToSalesReceiptResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by room number
     */
    public List<ExpenseResponse> getExpensesByRoomNo(String roomNo) {
        Room room = roomRepository.findByRoomNo(roomNo)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomNo));
        
        List<PostTransaction> transactions = postTransactionRepository.findByRoomId(room.getRoomId());
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by bill number
     */
    public List<ExpenseResponse> getExpensesByBillNo(String billNo) {
        List<PostTransaction> transactions = postTransactionRepository.findByBillNo(billNo);
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get expenses by folio number
     */
    public List<ExpenseResponse> getExpensesByFolioNo(String folioNo) {
        List<PostTransaction> transactions = postTransactionRepository.findByFolioNo(folioNo);
        return transactions.stream()
                .map(this::convertToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert PostTransaction entity to ExpenseResponse DTO
     */
    private ExpenseResponse convertToExpenseResponse(PostTransaction transaction) {
        ExpenseResponse response = new ExpenseResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setVoucherNo(transaction.getVoucherNo());
        response.setDate(transaction.getDate());
        response.setAccountHeadId(transaction.getAccHeadId());
        
        // Try to get account head name
        if (transaction.getAccHeadId() != null) {
            HotelAccountHead accountHead = hotelAccountHeadRepository.findById(transaction.getAccHeadId()).orElse(null);
            if (accountHead != null) {
                response.setAccountHeadName(accountHead.getName());
            }
        }
        
        response.setAmount(transaction.getAmount());
        response.setNarration(transaction.getNarration());
        response.setShiftNo(transaction.getShiftNo());
        response.setShiftDate(transaction.getShiftDate());
        
        // Add room information if available
        if (transaction.getRoomId() != null) {
            Room room = roomRepository.findById(transaction.getRoomId()).orElse(null);
            if (room != null) {
                // We'll add room number to narration or create a separate field if needed
            }
        }
        
        // Add bill information if available
        if (transaction.getBillNo() != null) {
            // We'll add bill number to narration or create a separate field if needed
        }
        
        // Add folio information if available
        if (transaction.getFolioNo() != null) {
            // We'll add folio number to narration or create a separate field if needed
        }
        
        return response;
    }
    
    /**
     * Convert SalesReceipt entity to SalesReceiptResponse DTO
     */
    private SalesReceiptResponse convertToSalesReceiptResponse(SalesReceipt receipt) {
        SalesReceiptResponse response = new SalesReceiptResponse();
        response.setReceiptNo(receipt.getReceiptNo());
        response.setDate(receipt.getDate());
        response.setModeOfPaymentId(receipt.getModeOfPaymentId());
        
        // Try to get mode of payment name
        // This would require a repository for mode of payment
        
        response.setAmount(receipt.getAmount());
        response.setVoucherNo(receipt.getVoucherNo());
        response.setNarration(receipt.getNarration());
        response.setShiftNo(receipt.getShiftNo());
        response.setShiftDate(receipt.getShiftDate());
        
        return response;
    }
}