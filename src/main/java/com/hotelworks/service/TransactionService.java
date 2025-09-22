package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.request.SalesReceiptRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.dto.response.SalesReceiptResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.SalesReceipt;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.SalesReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new expense transaction
     */
    public ExpenseResponse createExpense(ExpenseRequest request) {
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
        
        // Save the transaction
        PostTransaction savedTransaction = postTransactionRepository.save(transaction);
        
        // Convert to response DTO
        return convertToExpenseResponse(savedTransaction);
    }
    
    /**
     * Create a new sales receipt
     */
    public SalesReceiptResponse createSalesReceipt(SalesReceiptRequest request) {
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
     * Convert PostTransaction entity to ExpenseResponse DTO
     */
    private ExpenseResponse convertToExpenseResponse(PostTransaction transaction) {
        ExpenseResponse response = new ExpenseResponse();
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
        response.setAmount(receipt.getAmount());
        response.setVoucherNo(receipt.getVoucherNo());
        response.setNarration(receipt.getNarration());
        response.setShiftNo(receipt.getShiftNo());
        response.setShiftDate(receipt.getShiftDate());
        return response;
    }
}