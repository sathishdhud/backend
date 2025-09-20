package com.hotelworks.service;

import com.hotelworks.dto.request.PostTransactionRequest;
import com.hotelworks.dto.response.PostTransactionResponse;
import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.FoBillRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.TaxationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    private TaxationRepository taxationRepository;
    
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
        
        // Note: Automatic bill update is disabled due to circular dependency
        // updateAssociatedBill(request.getFolioNo());
        
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
        
        // Handle GST inclusion - if amount includes GST, update it to include CGST and SGST
        BigDecimal finalAmount = request.getAmount();
        if (request.getIncludingGst() != null && "Y".equalsIgnoreCase(request.getIncludingGst())) {
            finalAmount = calculateAmountWithTaxes(request.getAmount());
        }
        transaction.setAmount(finalAmount);
        
        transaction.setNarration(request.getNarration());
        
        // Update room ID if provided
        if (request.getRoomId() != null) {
            transaction.setRoomId(request.getRoomId());
        }
        
        PostTransaction savedTransaction = postTransactionRepository.save(transaction);
        
        // Note: Automatic bill update is disabled due to circular dependency
        // Update associated bill if it exists
        // if (transaction.getFolioNo() != null) {
        //     updateAssociatedBill(transaction.getFolioNo());
        // } else if (transaction.getBillNo() != null) {
        //     updateBillTotal(transaction.getBillNo());
        // }
        
        return mapToPostTransactionResponse(savedTransaction);
    }
    
    /**
     * Delete transaction
     */
    public void deleteTransaction(String transactionId) {
        PostTransaction transaction = postTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
            
        String folioNo = transaction.getFolioNo();
        String billNo = transaction.getBillNo();
        
        if (!postTransactionRepository.existsById(transactionId)) {
            throw new RuntimeException("Transaction not found: " + transactionId);
        }
        postTransactionRepository.deleteById(transactionId);
        
        // Note: Automatic bill update is disabled due to circular dependency
        // Update associated bill if it exists
        // if (folioNo != null) {
        //     updateAssociatedBill(folioNo);
        // } else if (billNo != null) {
        //     updateBillTotal(billNo);
        // }
    }
    
    private PostTransaction createTransactionEntity(PostTransactionRequest request) {
        PostTransaction transaction = new PostTransaction();
        transaction.setTransactionId(numberGenerationService.generateTransactionId());
        transaction.setRoomId(request.getRoomId());
        transaction.setGuestName(request.getGuestName());
        transaction.setAccHeadId(request.getAccHeadId());
        transaction.setVoucherNo(request.getVoucherNo());
        
        // Handle GST inclusion - if amount includes GST, update it to include CGST and SGST
        BigDecimal finalAmount = request.getAmount();
        if (request.getIncludingGst() != null && "Y".equalsIgnoreCase(request.getIncludingGst())) {
            finalAmount = calculateAmountWithTaxes(request.getAmount());
        }
        transaction.setAmount(finalAmount);
        
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
    
    /**
     * Update the associated bill for a folio
     */
    private void updateAssociatedBill(String folioNo) {
        try {
            // Note: Due to circular dependency issues, we cannot directly call BillService here
            // This functionality would need to be handled differently, possibly through events
            // or by moving this logic to a higher level service
            System.out.println("Bill update needed for folio: " + folioNo + 
                " (Note: Automatic bill update is disabled due to circular dependency)");
        } catch (Exception e) {
            // Log the error but don't fail the transaction creation
            System.err.println("Failed to update associated bill for folio " + folioNo + ": " + e.getMessage());
        }
    }
    
    /**
     * Update bill total based on current transactions
     */
    private void updateBillTotal(String billNo) {
        try {
            // Note: Due to circular dependency issues, we cannot directly call BillService here
            // This functionality would need to be handled differently, possibly through events
            // or by moving this logic to a higher level service
            System.out.println("Bill total update needed for bill: " + billNo + 
                " (Note: Automatic bill update is disabled due to circular dependency)");
        } catch (Exception e) {
            // Log the error but don't fail the transaction creation
            System.err.println("Failed to update bill total for bill " + billNo + ": " + e.getMessage());
        }
    }
    
    /**
     * Calculate amount with taxes (CGST + SGST) when amount includes GST
     * @param baseAmount The base amount before taxes
     * @return The amount including CGST and SGST
     */
    public BigDecimal calculateAmountWithTaxes(BigDecimal baseAmount) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return baseAmount;
        }
        
        try {
            // Get CGST and SGST rates
            Optional<com.hotelworks.entity.Taxation> cgstTax = taxationRepository.findByTaxName("CGST");
            Optional<com.hotelworks.entity.Taxation> sgstTax = taxationRepository.findByTaxName("SGST");
            
            BigDecimal cgstRate = BigDecimal.ZERO;
            BigDecimal sgstRate = BigDecimal.ZERO;
            
            if (cgstTax.isPresent() && cgstTax.get().getPercentage() != null) {
                cgstRate = cgstTax.get().getPercentage();
            }
            
            if (sgstTax.isPresent() && sgstTax.get().getPercentage() != null) {
                sgstRate = sgstTax.get().getPercentage();
            }
            
            // Calculate total tax rate
            BigDecimal totalTaxRate = cgstRate.add(sgstRate);
            
            // Calculate amount including taxes
            // If amount already includes GST, we need to add the taxes to make it explicit
            // Amount with taxes = Base Amount * (1 + Total Tax Rate / 100)
            BigDecimal taxMultiplier = BigDecimal.valueOf(100).add(totalTaxRate)
                                        .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            
            return baseAmount.multiply(taxMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            // If there's any error in tax calculation, return the original amount
            System.err.println("Error calculating amount with taxes: " + e.getMessage());
            return baseAmount;
        }
    }
}