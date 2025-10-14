package com.hotelworks.scheduler;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Reservation;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.TaxationRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.service.NumberGenerationService;
import com.hotelworks.service.RoomStatusManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Scheduled job for automatic room status management
 */
@Component
public class RoomStatusScheduler {
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TaxationRepository taxationRepository;

    @Autowired
    private PostTransactionRepository postTransactionRepository;

    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Autowired
    private NumberGenerationService numberGenerationService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    /**
     * Runs daily at 6:00 AM to process room status updates for departure dates
     * This ensures rooms become available for new reservations automatically
     */
    @Scheduled(cron = "0 0 6 * * *") // Run at 6:00 AM every day
    public void processAutomaticRoomStatusUpdates() {
        try {
            System.out.println("Starting automatic room status updates...");
            
            RoomStatusManagementService.RoomStatusUpdateResult result = 
                roomStatusManagementService.processAutomaticRoomStatusUpdates();
            
            System.out.println("Room status update completed:");
            System.out.println("- Processing Date: " + result.getProcessingDate());
            System.out.println("- Total Checkouts: " + result.getTotalCheckouts());
            System.out.println("- Successful Updates: " + result.getSuccessfulUpdates());
            System.out.println("- Failed Updates: " + result.getFailedUpdates());
            
        } catch (Exception e) {
            System.err.println("Error during automatic room status updates: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs every 2 hours from 8 AM to 8 PM to check for overdue checkouts
     * This ensures guests who have overstayed are identified
     */
    @Scheduled(cron = "0 0 8,10,12,14,16,18,20 * * *") // Run at 8 AM, 10 AM, 12 PM, 2 PM, 4 PM, 6 PM, 8 PM
    public void checkForOverdueCheckouts() {
        try {
            System.out.println("Checking for overdue checkouts...");
            
            LocalDate currentDate = LocalDate.now();
            List<CheckIn> overdueGuests = checkInRepository.findOverdueCheckouts(currentDate);
            
            System.out.println("Found " + overdueGuests.size() + " overdue checkouts as of " + currentDate);
            
            for (CheckIn checkIn : overdueGuests) {
                try {
                    Room room = roomRepository.findById(checkIn.getRoomId()).orElse(null);
                    if (room != null) {
                        System.out.println("Overdue checkout found - Folio: " + checkIn.getFolioNo() + 
                                         ", Guest: " + checkIn.getGuestName() + 
                                         ", Room: " + room.getRoomNo() + 
                                         ", Expected Departure: " + checkIn.getDepartureDate());
                    }
                } catch (Exception e) {
                    System.err.println("Error processing overdue checkout for folio: " + checkIn.getFolioNo() + 
                                     " - Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error during overdue checkout check: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs daily at 1:00 AM to post room charges and taxes for all in-house guests
     * This ensures daily billing for ongoing stays with proper GST handling
     */
    @Scheduled(cron = "0 0 1 * * *") // Run at 1:00 AM every day
    public void postDailyRoomChargesAndTaxes() {
        try {
            System.out.println("Starting daily room charge and tax posting...");

            LocalDate auditDate = LocalDate.now();
            
            // Find all in-house guests
            List<CheckIn> inHouseGuests = checkInRepository.findInHouseGuests(auditDate);
            
            System.out.println("Found " + inHouseGuests.size() + " in-house guests for audit date: " + auditDate);
            
            int successfulPosts = 0;
            int skippedPosts = 0;
            int failedPosts = 0;
            
            for (CheckIn checkIn : inHouseGuests) {
                try {
                    // Check if room charges have already been posted for this audit date
                    List<PostTransaction> existingCharges = postTransactionRepository.findRoomChargesByFolioAndAuditDate(
                        checkIn.getFolioNo(), auditDate);
                    
                    if (!existingCharges.isEmpty()) {
                        // Room charges already posted for this audit date, skip
                        System.out.println("Skipping folio " + checkIn.getFolioNo() + " - charges already posted for " + auditDate);
                        skippedPosts++;
                        continue;
                    }
                    
                    // Get room details
                    Room room = roomRepository.findById(checkIn.getRoomId()).orElse(null);
                    if (room == null) {
                        System.err.println("Room not found for folio: " + checkIn.getFolioNo());
                        failedPosts++;
                        continue;
                    }
                    
                    // Get room rate - either from checkIn or room type
                    BigDecimal roomRate = checkIn.getRate();
                    if (roomRate == null && room.getRoomType() != null) {
                        // If room type has rate information, we could use that
                        // For now, we'll skip if no rate is available
                        System.err.println("No room rate available for folio: " + checkIn.getFolioNo());
                        failedPosts++;
                        continue;
                    }
                    
                    if (roomRate == null || roomRate.compareTo(BigDecimal.ZERO) <= 0) {
                        System.err.println("Invalid room rate for folio: " + checkIn.getFolioNo());
                        failedPosts++;
                        continue;
                    }
                    
                    // Check if the rate includes GST
                    boolean rateIncludesGst = false;
                    if (checkIn.getReservation() != null) {
                        rateIncludesGst = "Y".equalsIgnoreCase(checkIn.getReservation().getIncludingGst());
                    } else if (checkIn.getReservationNo() != null) {
                        // Try to load reservation to check GST inclusion
                        try {
                            Optional<Reservation> reservationOpt = reservationRepository.findById(checkIn.getReservationNo());
                            if (reservationOpt.isPresent()) {
                                rateIncludesGst = "Y".equalsIgnoreCase(reservationOpt.get().getIncludingGst());
                            }
                        } catch (Exception e) {
                            // Ignore, we'll assume GST is not included
                        }
                    }
                    
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
                    roomChargeTransaction.setAmount(baseRoomRate);
                    roomChargeTransaction.setNarration("Daily room charge for " + auditDate);
                    
                    Optional<HotelAccountHead> roomChargeAccountHead = hotelAccountHeadRepository.findByName("Room Charges");
                    if (roomChargeAccountHead.isPresent()) {
                        roomChargeTransaction.setAccHeadId(roomChargeAccountHead.get().getAccHeadId());
                    } else {
                        // Use a default account head ID if not found
                        roomChargeTransaction.setAccHeadId("ROOM_CHARGE");
                    }
                    
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
                        cgstTransaction.setAmount(cgstAmount);
                        cgstTransaction.setNarration("CGST on room charge for " + auditDate);
                        
                        // Try to find CGST account head or use default
                        Optional<HotelAccountHead> cgstAccountHead = hotelAccountHeadRepository.findByName("CGST");
                        if (cgstAccountHead.isPresent()) {
                            cgstTransaction.setAccHeadId(cgstAccountHead.get().getAccHeadId());
                        } else {
                            cgstTransaction.setAccHeadId("CGST");
                        }
                        
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
                        sgstTransaction.setAmount(sgstAmount);
                        sgstTransaction.setNarration("SGST on room charge for " + auditDate);
                        
                        // Try to find SGST account head or use default
                        Optional<HotelAccountHead> sgstAccountHead = hotelAccountHeadRepository.findByName("SGST");
                        if (sgstAccountHead.isPresent()) {
                            sgstTransaction.setAccHeadId(sgstAccountHead.get().getAccHeadId());
                        } else {
                            sgstTransaction.setAccHeadId("SGST");
                        }
                        
                        postTransactionRepository.save(sgstTransaction);
                    }
                    
                    successfulPosts++;
                    System.out.println("Successfully posted charges for folio: " + checkIn.getFolioNo() + 
                                     " (Room: " + room.getRoomNo() + ", Guest: " + checkIn.getGuestName() + ")" +
                                     " - Base: " + baseRoomRate + ", CGST: " + cgstAmount + ", SGST: " + sgstAmount);
                    
                } catch (Exception e) {
                    failedPosts++;
                    System.err.println("Failed to post charges for folio: " + checkIn.getFolioNo() + 
                                     " - Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Daily room charge posting completed:");
            System.out.println("- Audit Date: " + auditDate);
            System.out.println("- In-House Guests: " + inHouseGuests.size());
            System.out.println("- Successful Posts: " + successfulPosts);
            System.out.println("- Skipped Posts: " + skippedPosts);
            System.out.println("- Failed Posts: " + failedPosts);
            
        } catch (Exception e) {
            System.err.println("Error during daily room charge posting: " + e.getMessage());
            e.printStackTrace();
        }
    }
}