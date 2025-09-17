package com.hotelworks.service;

import com.hotelworks.dto.request.SplitBillRequest;
import com.hotelworks.dto.request.SplitBillTransactionItem;
import com.hotelworks.dto.request.BillSettlementRequest;
import com.hotelworks.dto.request.BillUpdateRequest;
import com.hotelworks.dto.response.*;
import com.hotelworks.entity.*;
import com.hotelworks.exception.SplitBillValidationException;
import com.hotelworks.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillService {
    
    @Autowired
    private FoBillRepository foBillRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PostTransactionService postTransactionService;
    
    @Autowired
    private AdvanceService advanceService;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private BillSettlementTypeRepository billSettlementTypeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Generate bill for checkout
     */
    public BillResponse generateBill(String folioNo) {
        CheckIn checkIn = checkInRepository.findById(folioNo)
            .orElseThrow(() -> new RuntimeException("Check-in not found: " + folioNo));
        
        // Ensure reservation data is loaded
        if (checkIn.getReservationNo() != null) {
            reservationRepository.findById(checkIn.getReservationNo())
                .ifPresent(checkIn::setReservation);
        }
        
        // Calculate total transaction amount
        BigDecimal totalTransactions = postTransactionRepository.getTotalTransactionsByFolio(folioNo);
        if (totalTransactions == null) {
            totalTransactions = BigDecimal.ZERO;
        }
        
        // Calculate total advances
        BigDecimal totalAdvances = advanceRepository.getTotalAdvancesByFolio(folioNo);
        if (totalAdvances == null) {
            totalAdvances = BigDecimal.ZERO;
        }
        
        // Create bill
        FoBill bill = new FoBill();
        bill.setBillNo(numberGenerationService.generateBillNumber());
        bill.setFolioNo(folioNo);
        bill.setGuestName(checkIn.getGuestName());
        bill.setRoomId(checkIn.getRoomId());
        bill.setTotalAmount(totalTransactions);
        bill.setAdvanceAmount(totalAdvances);
        
        // Initialize settlement status
        // Set paid amount to total advances (even if it exceeds total amount)
        bill.setPaidAmount(totalAdvances); // Advances count as paid amount
        bill.calculateBalanceAmount();
        bill.updateSettlementStatus();
        
        FoBill savedBill = foBillRepository.save(bill);
        
        // Update all transactions for this folio to reference the new bill
        List<PostTransaction> folioTransactions = postTransactionRepository.findByFolioNo(folioNo);
        for (PostTransaction transaction : folioTransactions) {
            transaction.setBillNo(savedBill.getBillNo());
            postTransactionRepository.save(transaction);
        }
        
        // Update all advances for this folio to reference the new bill
        List<Advance> folioAdvances = advanceRepository.findByFolioNo(folioNo);
        for (Advance advance : folioAdvances) {
            advance.setBillNo(savedBill.getBillNo());
            advanceRepository.save(advance);
        }
        
        return mapToBillResponse(savedBill);
    }
    
    /**
     * Get bill by bill number
     */
    public BillResponse getBill(String billNo) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        // Ensure checkIn and reservation data is loaded for balance calculation
        if (bill.getFolioNo() != null) {
            checkInRepository.findById(bill.getFolioNo())
                .ifPresent(checkIn -> {
                    bill.setCheckIn(checkIn);
                    if (checkIn.getReservationNo() != null) {
                        reservationRepository.findById(checkIn.getReservationNo())
                            .ifPresent(checkIn::setReservation);
                    }
                });
        }
        
        return mapToBillResponse(bill);
    }
    
    /**
     * Update bill by bill number
     */
    public BillResponse updateBill(String billNo, BillUpdateRequest request) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        // Update bill fields
        bill.setGuestName(request.getGuestName());
        bill.setTotalAmount(request.getTotalAmount());
        
        // Update advance amount if provided
        if (request.getAdvanceAmount() != null) {
            bill.setAdvanceAmount(request.getAdvanceAmount());
        }
        
        // Ensure paid amount doesn't exceed total amount
        if (bill.getPaidAmount() != null && bill.getTotalAmount() != null) {
            BigDecimal paidAmount = bill.getPaidAmount();
            BigDecimal totalAmount = bill.getTotalAmount();
            if (paidAmount.compareTo(totalAmount) > 0) {
                bill.setPaidAmount(totalAmount);
            }
        }
        
        // Update payment notes if provided
        if (request.getPaymentNotes() != null) {
            String currentNotes = bill.getPaymentNotes() != null ? bill.getPaymentNotes() : "";
            if (!currentNotes.isEmpty()) {
                bill.setPaymentNotes(currentNotes + "; " + request.getPaymentNotes());
            } else {
                bill.setPaymentNotes(request.getPaymentNotes());
            }
        }
        
        // Recalculate balance and settlement status
        bill.calculateBalanceAmount();
        bill.updateSettlementStatus();
        
        FoBill updatedBill = foBillRepository.save(bill);
        
        return mapToBillResponse(updatedBill);
    }
    
    /**
     * Get bill by folio number
     */
    public BillResponse getBillByFolio(String folioNo) {
        FoBill bill = foBillRepository.findByFolioNo(folioNo)
            .orElseThrow(() -> new RuntimeException("Bill not found for folio: " + folioNo));
        
        // Ensure checkIn and reservation data is loaded for balance calculation
        checkInRepository.findById(folioNo)
            .ifPresent(checkIn -> {
                bill.setCheckIn(checkIn);
                if (checkIn.getReservationNo() != null) {
                    reservationRepository.findById(checkIn.getReservationNo())
                        .ifPresent(checkIn::setReservation);
                }
            });
        
        return mapToBillResponse(bill);
    }
    
    /**
     * Search bills
     */
    public List<BillResponse> searchBills(String searchTerm) {
        List<FoBill> bills = foBillRepository.searchBills(searchTerm);
        return bills.stream()
            .map(this::mapToBillResponse)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get split bill preview - shows all transactions and advances for selection
     */
    public SplitBillPreviewResponse getSplitBillPreview(String billNo) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        SplitBillPreviewResponse preview = new SplitBillPreviewResponse();
        preview.setOriginalBillNo(bill.getBillNo());
        preview.setGuestName(bill.getGuestName());
        preview.setFolioNo(bill.getFolioNo());
        preview.setOriginalTotalAmount(bill.getTotalAmount());
        preview.setOriginalAdvanceAmount(bill.getAdvanceAmount());
        
        // Set room number
        roomRepository.findById(bill.getRoomId())
            .ifPresent(room -> preview.setRoomNo(room.getRoomNo()));
        
        // Get all transactions for this bill
        List<PostTransactionResponse> transactionResponses = postTransactionService.getTransactionsByBill(billNo);
        List<SplitBillTransactionResponse> splitTransactions = transactionResponses.stream()
            .map(this::mapToSplitBillTransaction)
            .collect(Collectors.toList());
        preview.setTransactions(splitTransactions);
        
        // Get all advances for this bill
        List<AdvanceResponse> advances = advanceService.getAdvancesByBill(billNo);
        preview.setAdvances(advances);
        
        return preview;
    }
    
    /**
     * Execute split bill operation
     */
    public SplitBillResponse splitBill(SplitBillRequest request) throws SplitBillValidationException {
        
        // Get original bill
        FoBill originalBill = foBillRepository.findById(request.getOriginalBillNo())
            .orElseThrow(() -> new RuntimeException("Original bill not found: " + request.getOriginalBillNo()));
        
        // Get selected transaction IDs
        List<String> selectedTransactionIds = request.getSelectedTransactions().stream()
            .filter(SplitBillTransactionItem::isSelectedForNewBill)
            .map(SplitBillTransactionItem::getTransactionId)
            .collect(Collectors.toList());
        
        if (selectedTransactionIds.isEmpty()) {
            throw new SplitBillValidationException("At least one transaction must be selected for new bill");
        }
        
        // Calculate amounts for selected transactions
        BigDecimal selectedTransactionsAmount = BigDecimal.ZERO;
        List<PostTransaction> selectedTransactions = selectedTransactionIds.stream()
            .map(id -> postTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id)))
            .collect(Collectors.toList());
        
        for (PostTransaction transaction : selectedTransactions) {
            selectedTransactionsAmount = selectedTransactionsAmount.add(transaction.getAmount());
        }
        
        // Calculate next split sequence number
        Long existingSplitsCount = foBillRepository.countSplitsByOriginalBill(originalBill.getBillNo());
        
        // Create new bill (split)
        FoBill newBill = new FoBill();
        newBill.setBillNo(numberGenerationService.generateBillNumber());
        newBill.setFolioNo(originalBill.getFolioNo());
        newBill.setGuestName(originalBill.getGuestName());
        newBill.setRoomId(originalBill.getRoomId());
        newBill.setTotalAmount(selectedTransactionsAmount);
        newBill.setAdvanceAmount(BigDecimal.ZERO);
        newBill.setPaidAmount(BigDecimal.ZERO);
        newBill.setOriginalBillNo(originalBill.getOriginalBillNo() != null ? 
            originalBill.getOriginalBillNo() : originalBill.getBillNo());
        newBill.setIsSplitBill(true);
        newBill.setSplitSequence(existingSplitsCount.intValue() + 1);
        
        // Set checkIn reference to ensure rate-based balance calculation works for split bills
        if (originalBill.getCheckIn() != null) {
            newBill.setCheckIn(originalBill.getCheckIn());
        } else if (originalBill.getFolioNo() != null) {
            // Load checkIn data for the new bill
            checkInRepository.findById(originalBill.getFolioNo())
                .ifPresent(checkIn -> {
                    newBill.setCheckIn(checkIn);
                    if (checkIn.getReservationNo() != null) {
                        reservationRepository.findById(checkIn.getReservationNo())
                            .ifPresent(checkIn::setReservation);
                    }
                });
        }
        
        FoBill savedNewBill = foBillRepository.save(newBill);
        
        // Update selected transactions to point to new bill
        for (PostTransaction transaction : selectedTransactions) {
            transaction.setBillNo(savedNewBill.getBillNo());
            postTransactionRepository.save(transaction);
        }
        
        // Update original bill amount
        BigDecimal remainingAmount = originalBill.getTotalAmount().subtract(selectedTransactionsAmount);
        originalBill.setTotalAmount(remainingAmount);
        // Recalculate balance and update settlement status using rate-based calculation
        originalBill.calculateBalanceAmount();
        originalBill.updateSettlementStatus();
        foBillRepository.save(originalBill);
        
        // Create response
        SplitBillResponse response = new SplitBillResponse();
        response.setOriginalBillNo(originalBill.getBillNo());
        response.setNewBillNo(savedNewBill.getBillNo());
        response.setGuestName(originalBill.getGuestName());
        response.setFolioNo(originalBill.getFolioNo());
        response.setSplitDateTime(LocalDateTime.now());
        response.setSplitNarration(request.getNewBillNarration());
        
        // Set room number
        roomRepository.findById(originalBill.getRoomId())
            .ifPresent(room -> response.setRoomNo(room.getRoomNo()));
        
        // Original bill amounts after split
        response.setOriginalBillRemainingAmount(remainingAmount);
        response.setOriginalBillAdvanceAmount(originalBill.getAdvanceAmount());
        // Calculate balance using rate-based calculation for consistency
        response.setOriginalBillBalanceAmount(originalBill.getBalanceAmount() != null ? 
            originalBill.getBalanceAmount() : BigDecimal.ZERO);
        
        // New bill amounts
        response.setNewBillTotalAmount(selectedTransactionsAmount);
        response.setNewBillAdvanceAmount(BigDecimal.ZERO);
        // Calculate balance for new bill using rate-based calculation
        savedNewBill.calculateBalanceAmount();
        response.setNewBillBalanceAmount(savedNewBill.getBalanceAmount() != null ? 
            savedNewBill.getBalanceAmount() : BigDecimal.ZERO);
        
        // Summary counts
        response.setTransactionsMovedToNewBill(selectedTransactions.size());
        
        // Count remaining transactions in original bill
        int remainingTransactions = postTransactionRepository.findByBillNo(originalBill.getBillNo()).size();
        response.setTransactionsRemainingInOriginal(remainingTransactions);
        
        return response;
    }
    
    /**
     * Get all related bills (original + all splits)
     */
    public List<BillResponse> getRelatedBills(String billNo) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        List<FoBill> relatedBills = new ArrayList<>();
        
        if (bill.getIsSplitBill() && bill.getOriginalBillNo() != null) {
            // This is a split bill, get original and all splits
            FoBill originalBill = foBillRepository.findById(bill.getOriginalBillNo())
                .orElseThrow(() -> new RuntimeException("Original bill not found: " + bill.getOriginalBillNo()));
            // Ensure checkIn and reservation data is loaded for balance calculation
            loadCheckInData(originalBill);
            // Recalculate balance using rate-based calculation
            originalBill.calculateBalanceAmount();
            relatedBills.add(originalBill);
            
            List<FoBill> splits = foBillRepository.findSplitBillsByOriginalBill(bill.getOriginalBillNo());
            // Ensure checkIn and reservation data is loaded for all splits
            for (FoBill split : splits) {
                loadCheckInData(split);
                // Recalculate balance using rate-based calculation
                split.calculateBalanceAmount();
            }
            relatedBills.addAll(splits);
        } else {
            // This is an original bill, get it and all its splits
            // Ensure checkIn and reservation data is loaded for balance calculation
            loadCheckInData(bill);
            // Recalculate balance using rate-based calculation
            bill.calculateBalanceAmount();
            relatedBills.add(bill);
            
            List<FoBill> splits = foBillRepository.findSplitBillsByOriginalBill(billNo);
            // Ensure checkIn and reservation data is loaded for all splits
            for (FoBill split : splits) {
                loadCheckInData(split);
                // Recalculate balance using rate-based calculation
                split.calculateBalanceAmount();
            }
            relatedBills.addAll(splits);
        }
        
        return relatedBills.stream()
            .map(this::mapToBillResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Load checkIn and reservation data for a bill to ensure proper balance calculation
     */
    private void loadCheckInData(FoBill bill) {
        if (bill.getFolioNo() != null) {
            checkInRepository.findById(bill.getFolioNo())
                .ifPresent(checkIn -> {
                    bill.setCheckIn(checkIn);
                    if (checkIn.getReservationNo() != null) {
                        reservationRepository.findById(checkIn.getReservationNo())
                            .ifPresent(checkIn::setReservation);
                    }
                });
        }
    }
    
    /**
     * Map PostTransactionResponse to SplitBillTransactionResponse
     */
    private SplitBillTransactionResponse mapToSplitBillTransaction(PostTransactionResponse transaction) {
        SplitBillTransactionResponse splitTransaction = new SplitBillTransactionResponse();
        splitTransaction.setTransactionId(transaction.getTransactionId());
        splitTransaction.setVoucherNo(transaction.getVoucherNo());
        splitTransaction.setDate(transaction.getDate());
        splitTransaction.setTransactionName(transaction.getAccHeadName());
        splitTransaction.setAmount(transaction.getAmount());
        splitTransaction.setNarration(transaction.getNarration());
        splitTransaction.setSelectedForNewBill(false); // Default to not selected
        return splitTransaction;
    }
    
    private BillResponse mapToBillResponse(FoBill bill) {
        BillResponse response = new BillResponse();
        response.setBillNo(bill.getBillNo() != null ? bill.getBillNo() : "");
        response.setFolioNo(bill.getFolioNo() != null ? bill.getFolioNo() : "");
        response.setGuestName(bill.getGuestName() != null ? bill.getGuestName() : "");
        response.setRoomId(bill.getRoomId() != null ? bill.getRoomId() : "");
        response.setTotalAmount(bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO);
        response.setAdvanceAmount(bill.getAdvanceAmount() != null ? bill.getAdvanceAmount() : BigDecimal.ZERO);
        response.setGeneratedAt(bill.getGeneratedAt());
        
        // Use the balance amount directly from the entity
        response.setBalanceAmount(bill.getBalanceAmount() != null ? bill.getBalanceAmount() : BigDecimal.ZERO);
        
        // Set room number using response population pattern
        if (bill.getRoomId() != null) {
            roomRepository.findById(bill.getRoomId())
                .ifPresent(room -> response.setRoomNo(room.getRoomNo() != null ? room.getRoomNo() : ""));
        } else {
            response.setRoomNo("");
        }
        
        // Get transactions - try bill first, then folio as fallback
        List<PostTransactionResponse> transactions = postTransactionService.getTransactionsByBill(bill.getBillNo());
        if (transactions.isEmpty() && bill.getFolioNo() != null) {
            // If no transactions found by bill, try by folio (for newly generated bills)
            transactions = postTransactionService.getTransactionsByFolio(bill.getFolioNo());
        }
        response.setTransactions(transactions);
        
        // Calculate and set total transaction amount
        BigDecimal totalTransactionAmount = BigDecimal.ZERO;
        for (PostTransactionResponse transaction : transactions) {
            if (transaction.getAmount() != null) {
                totalTransactionAmount = totalTransactionAmount.add(transaction.getAmount());
            }
        }
        response.setTotalTransactionAmount(totalTransactionAmount);
        
        // Get advances - try bill first, then folio as fallback  
        List<AdvanceResponse> advances = advanceService.getAdvancesByBill(bill.getBillNo());
        if (advances.isEmpty() && bill.getFolioNo() != null) {
            // If no advances found by bill, try by folio (for newly generated bills)
            advances = advanceService.getAdvancesByFolio(bill.getFolioNo());
        }
        response.setAdvances(advances);
        
        // Set split bill tracking fields
        response.setIsSplitBill(bill.getIsSplitBill() != null ? bill.getIsSplitBill() : false);
        response.setSplitSequence(bill.getSplitSequence());
        response.setOriginalBillNo(bill.getOriginalBillNo() != null ? bill.getOriginalBillNo() : "");
        
        // Set bill settlement fields
        response.setSettlementStatus(bill.getSettlementStatus() != null ? bill.getSettlementStatus() : "PENDING");
        response.setPaidAmount(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
        response.setSettlementDate(bill.getSettlementDate());
        response.setLastPaymentDate(bill.getLastPaymentDate());
        response.setPaymentNotes(bill.getPaymentNotes() != null ? bill.getPaymentNotes() : "");
        
        // Debug: Log what we found
        System.out.println("DEBUG: Bill " + bill.getBillNo() + " has " + transactions.size() + " transactions and " + advances.size() + " advances");
        
        return response;
    }
    
    /**
     * Process payment for a bill (deferred payment support)
     */
    public BillSettlementResponse processPayment(BillSettlementRequest request) {
        FoBill bill = foBillRepository.findById(request.getBillNo())
            .orElseThrow(() -> new RuntimeException("Bill not found: " + request.getBillNo()));
        
        // Validate payment mode
        if (!billSettlementTypeRepository.existsById(request.getModeOfPaymentId())) {
            throw new RuntimeException("Payment mode not found: " + request.getModeOfPaymentId());
        }
        
        // Update bill payment information
        BigDecimal currentPaidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        bill.setPaidAmount(currentPaidAmount.add(request.getPaymentAmount()));
        bill.setLastPaymentDate(LocalDateTime.now());
        
        // Update payment notes
        String currentNotes = bill.getPaymentNotes() != null ? bill.getPaymentNotes() : "";
        String newNote = "Payment: " + request.getPaymentAmount() + " via " + request.getModeOfPaymentId() + 
            " on " + LocalDateTime.now().toString().substring(0, 19);
        if (request.getPaymentNotes() != null && !request.getPaymentNotes().trim().isEmpty()) {
            newNote += " (" + request.getPaymentNotes() + ")";
        }
        bill.setPaymentNotes(currentNotes.isEmpty() ? newNote : currentNotes + "; " + newNote);
        
        // Update settlement status and balance
        bill.calculateBalanceAmount();
        bill.updateSettlementStatus();
        
        // Save bill
        FoBill savedBill = foBillRepository.save(bill);
        
        // Create payment advance record for tracking
        createPaymentAdvanceRecord(request, savedBill);
        
        return mapToBillSettlementResponse(savedBill, request);
    }
    
    /**
     * Get bill settlement status
     */
    public BillSettlementResponse getBillSettlementStatus(String billNo) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        return mapToBillSettlementResponse(bill, null);
    }
    
    /**
     * Get all pending bills (for deferred payment tracking)
     */
    public List<BillSettlementResponse> getPendingBills() {
        List<FoBill> pendingBills = foBillRepository.findBySettlementStatusIn(List.of("PENDING", "PARTIAL"));
        return pendingBills.stream()
            .map(bill -> mapToBillSettlementResponse(bill, null))
            .collect(Collectors.toList());
    }
    
    private BigDecimal calculateCurrentBalance(FoBill bill) {
        BigDecimal totalAmount = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal calculatedBalance = totalAmount.subtract(paidAmount);
        // Ensure balance never goes below zero
        return calculatedBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : calculatedBalance;
    }
    
    private void createPaymentAdvanceRecord(BillSettlementRequest request, FoBill bill) {
        // Create an advance record to track this payment
        Advance paymentRecord = new Advance();
        paymentRecord.setReceiptNo(numberGenerationService.generateAdvanceReceiptNumber());
        paymentRecord.setBillNo(bill.getBillNo());
        paymentRecord.setFolioNo(bill.getFolioNo());
        paymentRecord.setGuestName(bill.getGuestName());
        paymentRecord.setDate(java.time.LocalDate.now());
        paymentRecord.setAuditDate(java.time.LocalDate.now());
        paymentRecord.setShiftDate(java.time.LocalDate.now());
        paymentRecord.setShiftNo("1");
        paymentRecord.setModeOfPaymentId(request.getModeOfPaymentId());
        paymentRecord.setAmount(request.getPaymentAmount());
        paymentRecord.setCreditCardCompany(request.getCreditCardCompany());
        paymentRecord.setCardNumber(request.getCardNumber());
        paymentRecord.setOnlineCompanyName(request.getOnlineCompanyName());
        paymentRecord.setDetails(request.getDetails());
        paymentRecord.setNarration("Bill settlement payment for " + bill.getBillNo());
        
        advanceRepository.save(paymentRecord);
    }
    
    private BillSettlementResponse mapToBillSettlementResponse(FoBill bill, BillSettlementRequest request) {
        BillSettlementResponse response = new BillSettlementResponse();
        response.setBillNo(bill.getBillNo());
        response.setGuestName(bill.getGuestName());
        response.setFolioNo(bill.getFolioNo());
        response.setOriginalAmount(bill.getTotalAmount());
        response.setTotalPaidAmount(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
        response.setBalanceAmount(bill.getBalanceAmount() != null ? bill.getBalanceAmount() : BigDecimal.ZERO);
        response.setSettlementStatus(bill.getSettlementStatus());
        response.setLastPaymentDate(bill.getLastPaymentDate());
        response.setSettlementDate(bill.getSettlementDate());
        response.setPaymentNotes(bill.getPaymentNotes());
        
        // Set room number
        roomRepository.findById(bill.getRoomId())
            .ifPresent(room -> response.setRoomNo(room.getRoomNo()));
        
        // Set current payment details if this is a payment response
        if (request != null) {
            response.setCurrentPaymentAmount(request.getPaymentAmount());
            response.setModeOfPaymentId(request.getModeOfPaymentId());
            
            // Get payment mode name
            billSettlementTypeRepository.findById(request.getModeOfPaymentId())
                .ifPresent(paymentMode -> response.setModeOfPaymentName(paymentMode.getName()));
        }
        
        return response;
    }
}