package com.hotelworks.service;

import com.hotelworks.dto.request.AuditDateChangeRequest;
import com.hotelworks.dto.request.ShiftChangeRequest;
import com.hotelworks.dto.request.ShiftCloseRequest;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Hmsystem;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Refund;
import com.hotelworks.entity.Shift;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.HmsystemRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.RefundRepository;
import com.hotelworks.repository.ShiftRepository;
import com.hotelworks.repository.TaxationRepository;
import com.hotelworks.repository.AdvanceRepository;
import com.hotelworks.repository.SalesReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OperationsService {
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private ShiftRepository shiftRepository;
    
    @Autowired
    private HmsystemRepository hmsystemRepository;
    
    @Autowired
    private TaxationRepository taxationRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private SalesReceiptRepository salesReceiptRepository;
    
    @Autowired
    private RefundRepository refundRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Process audit date change - posts room charges and taxes for all in-house guests
     * Implements proper GST handling as per requirements:
     * - If GST is included in rate: Calculate base rate and split taxes
     * - If GST is not included: Add taxes to base rate
     */
    public String processAuditDateChange(AuditDateChangeRequest request) {
        if (!"YES".equals(request.getConfirmation())) {
            throw new RuntimeException("Audit date change requires confirmation");
        }
        
        LocalDate auditDate = LocalDate.now();
        List<CheckIn> inHouseGuests = checkInRepository.findInHouseGuests(auditDate);
        
        // Ensure required account heads exist
        ensureRequiredAccountHeadsExist();
        
        int processedCount = 0;
        int skippedCount = 0;
        
        for (CheckIn checkIn : inHouseGuests) {
            // Check if room charges have already been posted for this audit date
            List<PostTransaction> existingCharges = postTransactionRepository.findRoomChargesByFolioAndAuditDate(
                checkIn.getFolioNo(), auditDate);
            
            if (!existingCharges.isEmpty()) {
                // Room charges already posted for this audit date, skip
                System.out.println("Skipping folio " + checkIn.getFolioNo() + " - charges already posted for " + auditDate);
                skippedCount++;
                continue;
            }
            
            // Post room charges and taxes
            postRoomChargesAndTaxesForDate(checkIn, auditDate);
            processedCount++;
        }
        
        return String.format("Audit date change processed successfully. " +
                           "Room charges and taxes posted for %d in-house guests. %d guests skipped (already processed).", 
                           processedCount, skippedCount);
    }
    
    /**
     * Process shift change - updates shift table with all shift details
     */
    public String processShiftChange(ShiftChangeRequest request) {
        // Check if shift record already exists
        Shift existingShift = shiftRepository.findByShiftNoAndShiftDate(
            request.getShiftNo(), request.getShiftDate()).orElse(null);
        
        if (existingShift != null) {
            // Update existing shift
            existingShift.setOpeningBalance(request.getOpeningBalance());
            existingShift.setClosingBalance(request.getClosingBalance());
            existingShift.setTotalIncome(request.getTotalIncome());
            existingShift.setTotalExpense(request.getTotalExpense());
            shiftRepository.save(existingShift);
            return "Shift details updated successfully";
        } else {
            // Create new shift record
            Shift newShift = new Shift();
            newShift.setShiftNo(request.getShiftNo());
            newShift.setShiftDate(request.getShiftDate());
            newShift.setAuditDate(request.getShiftDate()); // Audit date same as shift date initially
            newShift.setOpeningBalance(request.getOpeningBalance());
            newShift.setClosingBalance(request.getClosingBalance());
            newShift.setTotalIncome(request.getTotalIncome());
            newShift.setTotalExpense(request.getTotalExpense());
            shiftRepository.save(newShift);
            return "New shift record created successfully";
        }
    }
    
    /**
     * Process shift close - handles shift rotation logic
     * Calculates proper closing balance based on:
     * (opening balance + total cash receipts) - (total expenses + total refunds)
     */
    public String processShiftClose(ShiftCloseRequest request) {
        // Get the latest HMS system record
        Optional<Hmsystem> latestHmsystemOpt = hmsystemRepository.findLatestRecord();
        
        if (!latestHmsystemOpt.isPresent()) {
            throw new RuntimeException("No HMS system record found");
        }
        
        Hmsystem hmsystem = latestHmsystemOpt.get();
        Integer runningShift = hmsystem.getRunningShift();
        Integer totalShift = hmsystem.getTotalShift();
        
        if (runningShift == null || totalShift == null) {
            throw new RuntimeException("Invalid HMS system configuration");
        }
        
        // Calculate financial figures for the shift
        LocalDate shiftDate = hmsystem.getShiftDate();
        String shiftNo = String.valueOf(runningShift);
        
        // Get total cash receipts (from advances, sales receipts, and settlements)
        BigDecimal totalCashReceipts = calculateTotalCashReceipts(shiftNo, shiftDate);
        
        // Get total expenses for the shift
        BigDecimal totalExpenses = calculateTotalExpenses(shiftNo, shiftDate);
        
        // Get total refunds for the shift
        BigDecimal totalRefunds = calculateTotalRefunds(shiftNo, shiftDate);
        
        // Calculate the proper closing balance
        // Formula: (opening balance + total cash receipts) - (total expenses + total refunds)
        BigDecimal calculatedClosingBalance = request.getOpeningBalance()
            .add(totalCashReceipts)
            .subtract(totalExpenses.add(totalRefunds));
        
        // Create shift record with current data and calculated figures
        Shift shift = new Shift();
        shift.setShiftNo(shiftNo);
        shift.setShiftDate(shiftDate);
        shift.setAuditDate(shiftDate); // Audit date same as shift date initially
        shift.setOpeningBalance(request.getOpeningBalance());
        shift.setClosingBalance(calculatedClosingBalance);
        shift.setTotalIncome(totalCashReceipts);
        shift.setTotalExpense(totalExpenses.add(totalRefunds)); // Expenses + Refunds
        shiftRepository.save(shift);
        
        // Check if this is the last shift
        if (runningShift.equals(totalShift)) {
            // Last shift - increment date and reset running shift
            Hmsystem newHmsystem = new Hmsystem();
            newHmsystem.setShiftDate(hmsystem.getShiftDate().plusDays(1));
            newHmsystem.setRunningShift(1);
            newHmsystem.setTotalShift(totalShift);
            hmsystemRepository.save(newHmsystem);
            
            // Ensure required account heads exist before processing audit date change
            ensureRequiredAccountHeadsExist();
            
            // Also trigger audit date change for the new date
            processAutomaticAuditDateChange(newHmsystem.getShiftDate());
            
            return String.format("Shift %d closed successfully. Date changed to %s and running shift reset to 1. Audit date also updated. All shift details stored in shift table. " +
                               "Calculated closing balance: %s (Opening: %s, Receipts: %s, Expenses: %s, Refunds: %s)",
                runningShift, newHmsystem.getShiftDate().toString(), calculatedClosingBalance,
                request.getOpeningBalance(), totalCashReceipts, totalExpenses, totalRefunds);
        } else {
            // Not the last shift - just increment running shift
            hmsystem.setRunningShift(runningShift + 1);
            hmsystemRepository.save(hmsystem);
            
            return String.format("Shift %d closed successfully. Running shift incremented to %d. All shift details stored in shift table. " +
                               "Calculated closing balance: %s (Opening: %s, Receipts: %s, Expenses: %s, Refunds: %s)",
                runningShift, hmsystem.getRunningShift(), calculatedClosingBalance,
                request.getOpeningBalance(), totalCashReceipts, totalExpenses, totalRefunds);
        }
    }
    
    /**
     * Calculate total cash receipts for a shift from advances, sales receipts, and settlements
     */
    private BigDecimal calculateTotalCashReceipts(String shiftNo, LocalDate shiftDate) {
        // Get total cash advances for the shift
        BigDecimal cashAdvances = advanceRepository.getTotalCashAdvancesByShift(shiftNo, shiftDate);
        if (cashAdvances == null) {
            cashAdvances = BigDecimal.ZERO;
        }
        
        // Get total cash sales receipts for the shift
        BigDecimal cashSales = salesReceiptRepository.getTotalCashSalesByShift(shiftNo, shiftDate);
        if (cashSales == null) {
            cashSales = BigDecimal.ZERO;
        }
        
        // TODO: Add cash settlements when settlement functionality is implemented
        
        return cashAdvances.add(cashSales);
    }
    
    /**
     * Calculate total expenses for a shift
     */
    private BigDecimal calculateTotalExpenses(String shiftNo, LocalDate shiftDate) {
        BigDecimal totalExpenses = postTransactionRepository.getTotalExpensesByShift(shiftNo, shiftDate);
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }
    
    /**
     * Calculate total refunds for a shift
     */
    private BigDecimal calculateTotalRefunds(String shiftNo, LocalDate shiftDate) {
        BigDecimal totalRefunds = refundRepository.getTotalRefundsByShift(shiftNo, shiftDate);
        return totalRefunds != null ? totalRefunds : BigDecimal.ZERO;
    }
    
    /**
     * Ensure required account heads exist in the system
     */
    private void ensureRequiredAccountHeadsExist() {
        // Check and create ROOM_CHARGES account head if it doesn't exist
        if (!hotelAccountHeadRepository.existsByAccHeadId("ROOM_CHARGES")) {
            HotelAccountHead roomChargesHead = new HotelAccountHead();
            roomChargesHead.setAccHeadId("ROOM_CHARGES");
            roomChargesHead.setName("Room Charges");
            hotelAccountHeadRepository.save(roomChargesHead);
        }
        
        // Check and create CGST account head if it doesn't exist
        if (!hotelAccountHeadRepository.existsByAccHeadId("CGST")) {
            HotelAccountHead cgstHead = new HotelAccountHead();
            cgstHead.setAccHeadId("CGST");
            cgstHead.setName("Central Goods and Services Tax");
            hotelAccountHeadRepository.save(cgstHead);
        }
        
        // Check and create SGST account head if it doesn't exist
        if (!hotelAccountHeadRepository.existsByAccHeadId("SGST")) {
            HotelAccountHead sgstHead = new HotelAccountHead();
            sgstHead.setAccHeadId("SGST");
            sgstHead.setName("State Goods and Services Tax");
            hotelAccountHeadRepository.save(sgstHead);
        }
    }
    
    /**
     * Process automatic audit date change - posts room charges and taxes for all in-house guests
     * This is called automatically when the shift date changes
     */
    private void processAutomaticAuditDateChange(LocalDate auditDate) {
        // Ensure required account heads exist
        ensureRequiredAccountHeadsExist();
        
        List<CheckIn> inHouseGuests = checkInRepository.findInHouseGuests(auditDate);
        
        int processedCount = 0;
        int skippedCount = 0;
        
        for (CheckIn checkIn : inHouseGuests) {
            // Check if room charges have already been posted for this audit date
            List<PostTransaction> existingCharges = postTransactionRepository.findRoomChargesByFolioAndAuditDate(
                checkIn.getFolioNo(), auditDate);
            
            if (!existingCharges.isEmpty()) {
                // Room charges already posted for this audit date, skip
                System.out.println("Skipping folio " + checkIn.getFolioNo() + " - charges already posted for " + auditDate);
                skippedCount++;
                continue;
            }
            
            // Post room charges and taxes
            postRoomChargesAndTaxesForDate(checkIn, auditDate);
            processedCount++;
        }
        
        System.out.println(String.format("Automatic audit date change processed for date %s. Room charges and taxes posted for %d in-house guests. %d guests skipped (already processed).", 
            auditDate.toString(), processedCount, skippedCount));
    }
    
    /**
     * Post room charges and taxes for a specific date
     * Implements proper GST handling as per requirements:
     * - If GST is included in rate: Calculate base rate and split taxes
     * - If GST is not included: Add taxes to base rate
     */
    private void postRoomChargesAndTaxesForDate(CheckIn checkIn, LocalDate auditDate) {
        if (checkIn.getRate() == null || checkIn.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Skipping folio " + checkIn.getFolioNo() + " - no valid rate defined");
            return; // Skip if no rate defined
        }
        
        // Check if the rate includes GST
        boolean rateIncludesGst = false;
        if (checkIn.getReservation() != null) {
            rateIncludesGst = "Y".equalsIgnoreCase(checkIn.getReservation().getIncludingGst());
        }
        
        BigDecimal roomRate = checkIn.getRate();
        BigDecimal baseRoomRate;
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        
        if (rateIncludesGst) {
            // Rate includes GST (5% CGST + 5% SGST = 10% total)
            // Calculate base rate from inclusive rate
            // Base Rate = Inclusive Rate / (1 + GST Rate)
            // For 10% GST: Base Rate = Inclusive Rate / 1.10
            BigDecimal divisor = BigDecimal.valueOf(1.10);
            baseRoomRate = roomRate.divide(divisor, 2, RoundingMode.HALF_UP);
            
            // Calculate tax amounts (5% each)
            cgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
            sgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
            
            // Adjust for rounding differences
            BigDecimal totalTaxes = cgstAmount.add(sgstAmount);
            BigDecimal rateDifference = roomRate.subtract(baseRoomRate.add(totalTaxes));
            if (rateDifference.compareTo(BigDecimal.ZERO) != 0) {
                // Add difference to base rate to maintain total amount
                baseRoomRate = baseRoomRate.add(rateDifference);
            }
        } else {
            // Rate does not include GST
            baseRoomRate = roomRate;
            // Calculate tax amounts (5% each)
            cgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
            sgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
        }
        
        // Post room charge transaction
        PostTransaction roomChargeTransaction = new PostTransaction();
        roomChargeTransaction.setTransactionId(numberGenerationService.generateTransactionId());
        roomChargeTransaction.setFolioNo(checkIn.getFolioNo());
        roomChargeTransaction.setRoomId(checkIn.getRoomId());
        roomChargeTransaction.setGuestName(checkIn.getGuestName());
        roomChargeTransaction.setDate(auditDate);
        roomChargeTransaction.setAuditDate(auditDate);
        roomChargeTransaction.setAccHeadId("ROOM_CHARGES");
        roomChargeTransaction.setAmount(baseRoomRate);
        roomChargeTransaction.setNarration("Room charges for " + auditDate + " (Audit date change)");
        
        postTransactionRepository.save(roomChargeTransaction);
        
        // Post CGST transaction if applicable
        if (cgstAmount.compareTo(BigDecimal.ZERO) > 0) {
            PostTransaction cgstTransaction = new PostTransaction();
            cgstTransaction.setTransactionId(numberGenerationService.generateTransactionId());
            cgstTransaction.setFolioNo(checkIn.getFolioNo());
            cgstTransaction.setRoomId(checkIn.getRoomId());
            cgstTransaction.setGuestName(checkIn.getGuestName());
            cgstTransaction.setDate(auditDate);
            cgstTransaction.setAuditDate(auditDate);
            cgstTransaction.setAccHeadId("CGST");
            cgstTransaction.setAmount(cgstAmount);
            cgstTransaction.setNarration("CGST for " + auditDate + " (Audit date change)");
            
            postTransactionRepository.save(cgstTransaction);
        }
        
        // Post SGST transaction if applicable
        if (sgstAmount.compareTo(BigDecimal.ZERO) > 0) {
            PostTransaction sgstTransaction = new PostTransaction();
            sgstTransaction.setTransactionId(numberGenerationService.generateTransactionId());
            sgstTransaction.setFolioNo(checkIn.getFolioNo());
            sgstTransaction.setRoomId(checkIn.getRoomId());
            sgstTransaction.setGuestName(checkIn.getGuestName());
            sgstTransaction.setDate(auditDate);
            sgstTransaction.setAuditDate(auditDate);
            sgstTransaction.setAccHeadId("SGST");
            sgstTransaction.setAmount(sgstAmount);
            sgstTransaction.setNarration("SGST for " + auditDate + " (Audit date change)");
            
            postTransactionRepository.save(sgstTransaction);
        }
        
        System.out.println("Posted charges for folio: " + checkIn.getFolioNo() + 
                         " - Base: " + baseRoomRate + 
                         ", CGST: " + cgstAmount + 
                         ", SGST: " + sgstAmount);
    }
    
    /**
     * Get the latest HMS system information
     */
    public Hmsystem getLatestHmsystemInfo() {
        Optional<Hmsystem> latestHmsystemOpt = hmsystemRepository.findLatestRecord();
        
        if (!latestHmsystemOpt.isPresent()) {
            throw new RuntimeException("No HMS system record found");
        }
        
        return latestHmsystemOpt.get();
    }
    
    /**
     * Process day end - updates HMSSYSTEM table and increases audit date by 1 day
     */
    public String processDayEnd(String confirmation) {
        if (!"YES".equals(confirmation)) {
            throw new RuntimeException("Day end process requires confirmation with 'YES'");
        }
        
        // Get the latest HMS system record
        Optional<Hmsystem> latestHmsystemOpt = hmsystemRepository.findLatestRecord();
        
        if (!latestHmsystemOpt.isPresent()) {
            throw new RuntimeException("No HMS system record found");
        }
        
        Hmsystem hmsystem = latestHmsystemOpt.get();
        LocalDate currentAuditDate = hmsystem.getShiftDate();
        LocalDate newAuditDate = currentAuditDate.plusDays(1);
        
        // Update the HMS system record with the new audit date
        Hmsystem newHmsystem = new Hmsystem();
        newHmsystem.setShiftDate(newAuditDate);
        newHmsystem.setRunningShift(1); // Reset to first shift
        newHmsystem.setTotalShift(hmsystem.getTotalShift()); // Keep the same total shifts
        
        hmsystemRepository.save(newHmsystem);
        
        // Also trigger audit date change for the new date
        processAutomaticAuditDateChange(newAuditDate);
        
        return String.format("Day end process completed successfully. Audit date changed from %s to %s. Software will now shut down.", 
                           currentAuditDate.toString(), newAuditDate.toString());
    }
}