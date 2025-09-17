package com.hotelworks.service;

import com.hotelworks.dto.request.PostTransactionRequest;
import com.hotelworks.dto.response.PostTransactionResponse;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.FoBillRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostTransactionService {
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private FoBillRepository foBillRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create transaction for in-house guest
     */
    public PostTransactionResponse createTransactionForInHouseGuest(PostTransactionRequest request) {
        validateTransactionRequest(request);
        
        if (request.getFolioNo() == null) {
            throw new RuntimeException("Folio number is required for in-house guest transaction");
        }
        
        if (!checkInRepository.existsById(request.getFolioNo())) {
            throw new RuntimeException("Check-in not found: " + request.getFolioNo());
        }
        
        PostTransaction transaction = createTransactionEntity(request);
        transaction.setFolioNo(request.getFolioNo());
        transaction.setDate(LocalDate.now()); // Use audit date for in-house guests
        transaction.setAuditDate(LocalDate.now());
        
        PostTransaction savedTransaction = postTransactionRepository.save(transaction);
        return mapToPostTransactionResponse(savedTransaction);
    }
    
    /**
     * Create transaction for checkout guest
     */
    public PostTransactionResponse createTransactionForCheckoutGuest(PostTransactionRequest request) {
        validateTransactionRequest(request);
        
        if (request.getBillNo() == null) {
            throw new RuntimeException("Bill number is required for checkout guest transaction");
        }
        
        if (request.getDate() == null) {
            throw new RuntimeException("Date is required for checkout guest transaction");
        }
        
        if (!foBillRepository.existsById(request.getBillNo())) {
            throw new RuntimeException("Bill not found: " + request.getBillNo());
        }
        
        PostTransaction transaction = createTransactionEntity(request);
        transaction.setBillNo(request.getBillNo());
        transaction.setDate(request.getDate()); // Manual date entry
        transaction.setAuditDate(request.getDate());
        
        PostTransaction savedTransaction = postTransactionRepository.save(transaction);
        return mapToPostTransactionResponse(savedTransaction);
    }
    
    /**
     * Get transactions by folio number
     */
    public List<PostTransactionResponse> getTransactionsByFolio(String folioNo) {
        List<PostTransaction> transactions = postTransactionRepository.findByFolioNoOrderByDateDesc(folioNo);
        return transactions.stream()
            .map(this::mapToPostTransactionResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get transactions by bill number
     */
    public List<PostTransactionResponse> getTransactionsByBill(String billNo) {
        List<PostTransaction> transactions = postTransactionRepository.findByBillNo(billNo);
        return transactions.stream()
            .map(this::mapToPostTransactionResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get transactions by room ID
     */
    public List<PostTransactionResponse> getTransactionsByRoom(String roomId) {
        List<PostTransaction> transactions = postTransactionRepository.findByRoomId(roomId);
        return transactions.stream()
            .map(this::mapToPostTransactionResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get total transactions by folio
     */
    public BigDecimal getTotalTransactionsByFolio(String folioNo) {
        BigDecimal total = postTransactionRepository.getTotalTransactionsByFolio(folioNo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get total transactions by bill
     */
    public BigDecimal getTotalTransactionsByBill(String billNo) {
        BigDecimal total = postTransactionRepository.getTotalTransactionsByBill(billNo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get transactions between dates
     */
    public List<PostTransactionResponse> getTransactionsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<PostTransaction> transactions = postTransactionRepository.findTransactionsBetweenDates(startDate, endDate);
        return transactions.stream()
            .map(this::mapToPostTransactionResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get transaction by ID
     */
    public PostTransactionResponse getTransaction(String transactionId) {
        PostTransaction transaction = postTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        return mapToPostTransactionResponse(transaction);
    }
    
    /**
     * Update transaction
     */
    public PostTransactionResponse updateTransaction(String transactionId, PostTransactionRequest request) {
        PostTransaction transaction = postTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        
        validateTransactionRequest(request);
        
        // Update editable fields
        transaction.setGuestName(request.getGuestName());
        transaction.setAccHeadId(request.getAccHeadId());
        transaction.setVoucherNo(request.getVoucherNo());
        transaction.setAmount(request.getAmount());
        transaction.setNarration(request.getNarration());
        
        // Update room ID if provided
        if (request.getRoomId() != null) {
            transaction.setRoomId(request.getRoomId());
        }
        
        PostTransaction savedTransaction = postTransactionRepository.save(transaction);
        return mapToPostTransactionResponse(savedTransaction);
    }
    
    /**
     * Delete transaction
     */
    public void deleteTransaction(String transactionId) {
        if (!postTransactionRepository.existsById(transactionId)) {
            throw new RuntimeException("Transaction not found: " + transactionId);
        }
        postTransactionRepository.deleteById(transactionId);
    }
    
    private PostTransaction createTransactionEntity(PostTransactionRequest request) {
        PostTransaction transaction = new PostTransaction();
        transaction.setTransactionId(numberGenerationService.generateTransactionId());
        transaction.setRoomId(request.getRoomId());
        transaction.setGuestName(request.getGuestName());
        transaction.setAccHeadId(request.getAccHeadId());
        transaction.setVoucherNo(request.getVoucherNo());
        transaction.setAmount(request.getAmount());
        transaction.setNarration(request.getNarration());
        
        return transaction;
    }
    
    private void validateTransactionRequest(PostTransactionRequest request) {
        if (!hotelAccountHeadRepository.existsById(request.getAccHeadId())) {
            throw new RuntimeException("Account head not found: " + request.getAccHeadId());
        }
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }
    
    private PostTransactionResponse mapToPostTransactionResponse(PostTransaction transaction) {
        PostTransactionResponse response = new PostTransactionResponse();
        response.setTransactionId(transaction.getTransactionId() != null ? transaction.getTransactionId() : "");
        response.setFolioNo(transaction.getFolioNo() != null ? transaction.getFolioNo() : "");
        response.setBillNo(transaction.getBillNo() != null ? transaction.getBillNo() : "");
        response.setRoomId(transaction.getRoomId() != null ? transaction.getRoomId() : "");
        response.setGuestName(transaction.getGuestName() != null ? transaction.getGuestName() : "");
        response.setDate(transaction.getDate());
        response.setAuditDate(transaction.getAuditDate());
        response.setAccHeadId(transaction.getAccHeadId() != null ? transaction.getAccHeadId() : "");
        response.setVoucherNo(transaction.getVoucherNo() != null ? transaction.getVoucherNo() : "");
        response.setAmount(transaction.getAmount() != null ? transaction.getAmount() : BigDecimal.ZERO);
        response.setNarration(transaction.getNarration() != null ? transaction.getNarration() : "");
        
        // Fetch room number by roomId from repository (consistent pattern)
        if (transaction.getRoomId() != null) {
            roomRepository.findById(transaction.getRoomId())
                .ifPresent(room -> response.setRoomNo(room.getRoomNo() != null ? room.getRoomNo() : ""));
        } else {
            response.setRoomNo("");
        }
        
        // Fetch account head name by accHeadId from repository (consistent pattern)
        if (transaction.getAccHeadId() != null) {
            hotelAccountHeadRepository.findById(transaction.getAccHeadId())
                .ifPresent(accountHead -> response.setAccHeadName(accountHead.getName() != null ? accountHead.getName() : ""));
        } else {
            response.setAccHeadName("");
        }
        
        return response;
    }
}