package com.hotelworks.service;

import com.hotelworks.dto.request.AuditDateChangeRequest;
import com.hotelworks.dto.request.ShiftChangeRequest;
import com.hotelworks.dto.request.ShiftCloseRequest;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Hmsystem;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Shift;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.HmsystemRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.ShiftRepository;
import com.hotelworks.repository.TaxationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OperationsService {
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private ShiftRepository shiftRepository;
    
    @Autowired
    private HmsystemRepository hmsystemRepository;
    
    @Autowired
    private TaxationRepository taxationRepository;
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Process audit date change - posts room charges and taxes for all in-house guests
     */
    public String processAuditDateChange(AuditDateChangeRequest request) {
        if (!"YES".equals(request.getConfirmation())) {
            throw new RuntimeException("Audit date change requires confirmation");
        }
        
        LocalDate currentDate = LocalDate.now();
        List<CheckIn> inHouseGuests = checkInRepository.findInHouseGuests(currentDate);
        
        // Ensure required account heads exist
        ensureRequiredAccountHeadsExist();
        
        int processedCount = 0;
        
        for (CheckIn checkIn : inHouseGuests) {
            // Post room charges
            postRoomCharges(checkIn);
            
            // Post taxes (CGST and SGST)
            postTaxes(checkIn);
            
            processedCount++;
        }
        
        return String.format("Audit date change processed successfully. " +
                           "Room charges and taxes posted for %d in-house guests.", processedCount);
    }
    
    /**
     * Process shift change - updates shift table with balance
     */
    public String processShiftChange(ShiftChangeRequest request) {
        // Check if shift record already exists
        Shift existingShift = shiftRepository.findByShiftNoAndShiftDate(
            request.getShiftNo(), request.getShiftDate()).orElse(null);
        
        if (existingShift != null) {
            // Update existing shift
            existingShift.setBalance(request.getBalance());
            shiftRepository.save(existingShift);
            return "Shift balance updated successfully";
        } else {
            // Create new shift record
            Shift newShift = new Shift();
            newShift.setShiftNo(request.getShiftNo());
            newShift.setShiftDate(request.getShiftDate());
            newShift.setBalance(request.getBalance());
            shiftRepository.save(newShift);
            return "New shift record created successfully";
        }
    }
    
    /**
     * Process shift close - handles shift rotation logic
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
        
        // Create shift record with current data
        Shift shift = new Shift();
        shift.setShiftNo(String.valueOf(runningShift));
        shift.setShiftDate(hmsystem.getShiftDate());
        shift.setBalance(request.getBalance());
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
            
            return String.format("Shift %d closed successfully. Date changed to %s and running shift reset to 1. Audit date also updated. Balance stored in shift table.",
                runningShift, newHmsystem.getShiftDate().toString());
        } else {
            // Not the last shift - just increment running shift
            hmsystem.setRunningShift(runningShift + 1);
            hmsystemRepository.save(hmsystem);
            
            return String.format("Shift %d closed successfully. Running shift incremented to %d. Balance stored in shift table.",
                runningShift, hmsystem.getRunningShift());
        }
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
        
        for (CheckIn checkIn : inHouseGuests) {
            // Post room charges
            postRoomChargesForDate(checkIn, auditDate);
            
            // Post taxes (CGST and SGST)
            postTaxesForDate(checkIn, auditDate);
            
            processedCount++;
        }
        
        System.out.println(String.format("Automatic audit date change processed for date %s. Room charges and taxes posted for %d in-house guests.", 
            auditDate.toString(), processedCount));
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
    
    private void postRoomCharges(CheckIn checkIn) {
        postRoomChargesForDate(checkIn, LocalDate.now());
    }
    
    private void postRoomChargesForDate(CheckIn checkIn, LocalDate auditDate) {
        if (checkIn.getRate() == null || checkIn.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            return; // Skip if no rate defined
        }
        
        PostTransaction roomChargeTransaction = new PostTransaction();
        roomChargeTransaction.setTransactionId(numberGenerationService.generateTransactionId());
        roomChargeTransaction.setFolioNo(checkIn.getFolioNo());
        roomChargeTransaction.setRoomId(checkIn.getRoomId());
        roomChargeTransaction.setGuestName(checkIn.getGuestName());
        roomChargeTransaction.setDate(auditDate);
        roomChargeTransaction.setAuditDate(auditDate);
        roomChargeTransaction.setAccHeadId("ROOM_CHARGES");
        roomChargeTransaction.setAmount(checkIn.getRate());
        roomChargeTransaction.setNarration("Room charges - Audit date change");
        
        postTransactionRepository.save(roomChargeTransaction);
    }
    
    private void postTaxes(CheckIn checkIn) {
        postTaxesForDate(checkIn, LocalDate.now());
    }
    
    private void postTaxesForDate(CheckIn checkIn, LocalDate auditDate) {
        if (checkIn.getRate() == null || checkIn.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            return; // Skip if no rate defined
        }
        
        // Post CGST
        BigDecimal cgstRate = BigDecimal.valueOf(9.0); // 9% CGST - should be configurable
        BigDecimal cgstAmount = checkIn.getRate().multiply(cgstRate).divide(BigDecimal.valueOf(100));
        
        PostTransaction cgstTransaction = new PostTransaction();
        cgstTransaction.setTransactionId(numberGenerationService.generateTransactionId());
        cgstTransaction.setFolioNo(checkIn.getFolioNo());
        cgstTransaction.setRoomId(checkIn.getRoomId());
        cgstTransaction.setGuestName(checkIn.getGuestName());
        cgstTransaction.setDate(auditDate);
        cgstTransaction.setAuditDate(auditDate);
        cgstTransaction.setAccHeadId("CGST");
        cgstTransaction.setAmount(cgstAmount);
        cgstTransaction.setNarration("CGST - Audit date change");
        
        postTransactionRepository.save(cgstTransaction);
        
        // Post SGST
        BigDecimal sgstRate = BigDecimal.valueOf(9.0); // 9% SGST - should be configurable
        BigDecimal sgstAmount = checkIn.getRate().multiply(sgstRate).divide(BigDecimal.valueOf(100));
        
        PostTransaction sgstTransaction = new PostTransaction();
        sgstTransaction.setTransactionId(numberGenerationService.generateTransactionId());
        sgstTransaction.setFolioNo(checkIn.getFolioNo());
        sgstTransaction.setRoomId(checkIn.getRoomId());
        sgstTransaction.setGuestName(checkIn.getGuestName());
        sgstTransaction.setDate(auditDate);
        sgstTransaction.setAuditDate(auditDate);
        sgstTransaction.setAccHeadId("SGST");
        sgstTransaction.setAmount(sgstAmount);
        sgstTransaction.setNarration("SGST - Audit date change");
        
        postTransactionRepository.save(sgstTransaction);
    }
}