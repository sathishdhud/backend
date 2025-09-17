package com.hotelworks.service;

import com.hotelworks.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NumberGenerationService {
    
    private static final String ACCOUNTING_YEAR_FORMAT = "dd/yy-yy";
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private FoBillRepository foBillRepository;
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    // Add repositories for entities that need sequential IDs
    @Autowired
    private HotelSoftUserRepository hotelSoftUserRepository;
    
    /**
     * Generate reservation number with accounting year format
     * Sequential numbering: 1-25-26, 2-25-26, 3-25-26, etc.
     */
    public synchronized String generateReservationNumber() {
        String accountingYear = getAccountingYear();
        
        // Count existing reservations for current accounting year
        long count = reservationRepository.count();
        int nextSequence = (int) (count + 1);
        
        return String.format("%d-%s", nextSequence, accountingYear);
    }
    
    /**
     * Generate folio number with accounting year format
     * Sequential numbering: F1-25-26, F2-25-26, F3-25-26, etc.
     */
    public synchronized String generateFolioNumber() {
        String accountingYear = getAccountingYear();
        
        // Count existing check-ins for current accounting year
        long count = checkInRepository.count();
        int nextSequence = (int) (count + 1);
        
        return String.format("F%d-%s", nextSequence, accountingYear);
    }
    
    /**
     * Generate bill number with accounting year format (URL-safe)
     * Sequential numbering: B1-25-26, B2-25-26, B3-25-26, etc.
     */
    public synchronized String generateBillNumber() {
        String accountingYear = getAccountingYear();
        
        // Count existing bills for current accounting year
        long count = foBillRepository.count();
        int nextSequence = (int) (count + 1);
        
        return String.format("B%d-%s", nextSequence, accountingYear);
    }
    
    /**
     * Generate split bill number from original bill number
     * Format: Original bill + split suffix (e.g., B3906-25-26-S1, B3906-25-26-S2)
     */
    public String generateSplitBillNumber(String originalBillNo, int splitIndex) {
        return String.format("%s-S%d", originalBillNo, splitIndex);
    }
    
    /**
     * Generate receipt number with accounting year format
     * Sequential numbering: R1-25-26, R2-25-26, R3-25-26, etc.
     */
    public synchronized String generateReceiptNumber() {
        String accountingYear = getAccountingYear();
        
        // Count existing advances for current accounting year
        long count = advanceRepository.count();
        int nextSequence = (int) (count + 1);
        
        return String.format("R%d-%s", nextSequence, accountingYear);
    }
    
    /**
     * Generate transaction ID
     * Sequential numbering: TXN1, TXN2, TXN3, etc.
     */
    public synchronized String generateTransactionId() {
        // Simple sequential numbering for transactions
        long timestamp = System.currentTimeMillis();
        return "TXN" + (timestamp % 100000); // Keep it shorter but still unique
    }
    
    /**
     * Generate plan ID
     */
    public String generatePlanId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("PLAN%04d", timestamp);
    }
    
    /**
     * Generate room type ID
     */
    public String generateRoomTypeId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("TYPE%04d", timestamp);
    }
    
    /**
     * Generate company ID
     */
    public String generateCompanyId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("COMP%04d", timestamp);
    }
    
    /**
     * Generate room ID
     */
    public String generateRoomId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("ROOM%04d", timestamp);
    }
    
    /**
     * Generate tax ID
     */
    public String generateTaxId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("TAX%04d", timestamp);
    }
    
    /**
     * Generate account head ID
     */
    public String generateAccountHeadId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("ACC%04d", timestamp);
    }
    
    /**
     * Generate nationality ID
     */
    public String generateNationalityId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("NAT%04d", timestamp);
    }
    
    /**
     * Generate ref mode ID
     */
    public String generateRefModeId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("REF%04d", timestamp);
    }
    
    /**
     * Generate arrival mode ID
     */
    public String generateArrivalModeId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("ARR%04d", timestamp);
    }
    
    /**
     * Generate reservation source ID
     */
    public String generateResvSourceId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("SRC%04d", timestamp);
    }
    
    /**
     * Generate user ID with sequential numbering
     * Format: 1-2025, 2-2025, 3-2025, etc.
     */
    public synchronized String generateUserId() {
        // Count existing users
        long count = hotelSoftUserRepository.count();
        int nextSequence = (int) (count + 1);
        
        // Get current year
        int currentYear = LocalDate.now().getYear();
        
        return String.format("USER-%d-%d", nextSequence, currentYear);
    }
    
    /**
     * Generate user rights ID
     */
    public String generateUserRightsId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("RIGHT%04d", timestamp);
    }
    
    /**
     * Generate user type ID
     */
    public String generateUserTypeId() {
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("UTYPE%04d", timestamp);
    }
    
    /**
     * Generate advance receipt number for payments
     * Sequential numbering: R1-25-26, R2-25-26, R3-25-26, etc.
     */
    public synchronized String generateAdvanceReceiptNumber() {
        String accountingYear = getAccountingYear();
        
        // Count existing advances for current accounting year
        long count = advanceRepository.count();
        int nextSequence = (int) (count + 1);
        
        return String.format("R%d-%s", nextSequence, accountingYear);
    }
    
    /**
     * Get accounting year in format yy-yy
     */
    private String getAccountingYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int month = currentDate.getMonthValue();
        
        // Assuming accounting year starts from April (month 4)
        if (month >= 4) {
            // Current year to next year
            return String.format("%02d-%02d", currentYear % 100, (currentYear + 1) % 100);
        } else {
            // Previous year to current year
            return String.format("%02d-%02d", (currentYear - 1) % 100, currentYear % 100);
        }
    }
}