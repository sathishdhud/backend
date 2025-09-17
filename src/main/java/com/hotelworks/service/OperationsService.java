package com.hotelworks.service;

import com.hotelworks.dto.request.AuditDateChangeRequest;
import com.hotelworks.dto.request.ShiftChangeRequest;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Shift;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.ShiftRepository;
import com.hotelworks.repository.TaxationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private TaxationRepository taxationRepository;
    
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
    
    private void postRoomCharges(CheckIn checkIn) {
        if (checkIn.getRate() == null || checkIn.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            return; // Skip if no rate defined
        }
        
        PostTransaction roomChargeTransaction = new PostTransaction();
        roomChargeTransaction.setTransactionId(numberGenerationService.generateTransactionId());
        roomChargeTransaction.setFolioNo(checkIn.getFolioNo());
        roomChargeTransaction.setRoomId(checkIn.getRoomId());
        roomChargeTransaction.setGuestName(checkIn.getGuestName());
        roomChargeTransaction.setDate(LocalDate.now());
        roomChargeTransaction.setAuditDate(LocalDate.now());
        roomChargeTransaction.setAccHeadId("ROOM_CHARGES"); // Should be configured
        roomChargeTransaction.setAmount(checkIn.getRate());
        roomChargeTransaction.setNarration("Room charges - Audit date change");
        
        postTransactionRepository.save(roomChargeTransaction);
    }
    
    private void postTaxes(CheckIn checkIn) {
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
        cgstTransaction.setDate(LocalDate.now());
        cgstTransaction.setAuditDate(LocalDate.now());
        cgstTransaction.setAccHeadId("CGST"); // Should be configured
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
        sgstTransaction.setDate(LocalDate.now());
        sgstTransaction.setAuditDate(LocalDate.now());
        sgstTransaction.setAccHeadId("SGST"); // Should be configured
        sgstTransaction.setAmount(sgstAmount);
        sgstTransaction.setNarration("SGST - Audit date change");
        
        postTransactionRepository.save(sgstTransaction);
    }
}