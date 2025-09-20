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
            
            if (!result.getUpdatedRooms().isEmpty()) {
                System.out.println("Updated Rooms:");
                for (RoomStatusManagementService.UpdatedRoomInfo room : result.getUpdatedRooms()) {
                    System.out.println("  - Room " + room.getRoomNo() + " (" + room.getRoomId() + 
                                     ") - Guest: " + room.getGuestName() + " - " + room.getNotes());
                }
            }
            
            if (!result.getFailedUpdatesList().isEmpty()) {
                System.out.println("Failed Updates:");
                for (RoomStatusManagementService.FailedUpdateInfo failed : result.getFailedUpdatesList()) {
                    System.out.println("  - Room " + failed.getRoomId() + ": " + failed.getError());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error during automatic room status update: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs every 2 hours during business hours to check for overdue checkouts
     * This helps staff identify guests who have overstayed
     */
    @Scheduled(cron = "0 0 8-20/2 * * *") // Run every 2 hours from 8 AM to 8 PM
    public void processOverdueCheckouts() {
        try {
            System.out.println("Checking for overdue checkouts...");
            
            RoomStatusManagementService.RoomStatusUpdateResult result = 
                roomStatusManagementService.processOverdueCheckouts();
            
            if (result.getTotalCheckouts() > 0) {
                System.out.println("Overdue checkout alert:");
                System.out.println("- Processing Date: " + result.getProcessingDate());
                System.out.println("- Overdue Checkouts Found: " + result.getTotalCheckouts());
                
                if (!result.getUpdatedRooms().isEmpty()) {
                    System.out.println("Overdue Rooms Requiring Attention:");
                    for (RoomStatusManagementService.UpdatedRoomInfo room : result.getUpdatedRooms()) {
                        System.out.println("  - Room " + room.getRoomNo() + " (" + room.getRoomId() + 
                                         ") - Guest: " + room.getGuestName() + " - " + room.getNotes());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error during overdue checkout check: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs daily at 1:00 AM to post room charges and taxes for all in-house guests
     * This ensures daily billing for ongoing stays
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
            int failedPosts = 0;
            
            // Get tax information
            Optional<Taxation> cgstTax = taxationRepository.findByTaxName("CGST");
            Optional<Taxation> sgstTax = taxationRepository.findByTaxName("SGST");
            
            // Get account head for room charges
            Optional<HotelAccountHead> roomChargeAccountHead = hotelAccountHeadRepository.findByName("Room Charges");
            
            for (CheckIn checkIn : inHouseGuests) {
                try {
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
                    
                    // Check if the rate already includes GST
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
                    
                    // Calculate taxes
                    BigDecimal cgstAmount = BigDecimal.ZERO;
                    BigDecimal sgstAmount = BigDecimal.ZERO;
                    BigDecimal baseRoomRate = roomRate;
                    
                    // If rate does not include GST, calculate taxes on the base rate
                    if (!rateIncludesGst && cgstTax.isPresent() && cgstTax.get().getPercentage() != null) {
                        cgstAmount = baseRoomRate.multiply(cgstTax.get().getPercentage())
                                            .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    }
                    
                    if (!rateIncludesGst && sgstTax.isPresent() && sgstTax.get().getPercentage() != null) {
                        sgstAmount = baseRoomRate.multiply(sgstTax.get().getPercentage())
                                            .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    }
                    
                    // If rate already includes GST, we need to extract the base rate
                    // For simplicity, we'll assume 18% total GST (9% CGST + 9% SGST)
                    if (rateIncludesGst) {
                        // Calculate base rate from inclusive rate
                        // Base Rate = Inclusive Rate / (1 + GST Rate)
                        BigDecimal totalGstRate = BigDecimal.valueOf(18); // 9% CGST + 9% SGST
                        BigDecimal divisor = BigDecimal.valueOf(100).add(totalGstRate)
                                                    .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
                        baseRoomRate = roomRate.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
                        
                        // Calculate tax amounts based on base rate
                        if (cgstTax.isPresent() && cgstTax.get().getPercentage() != null) {
                            cgstAmount = baseRoomRate.multiply(cgstTax.get().getPercentage())
                                                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                        }
                        
                        if (sgstTax.isPresent() && sgstTax.get().getPercentage() != null) {
                            sgstAmount = baseRoomRate.multiply(sgstTax.get().getPercentage())
                                                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                    
                    // Post room charge transaction
                    PostTransaction roomChargeTransaction = new PostTransaction();
                    roomChargeTransaction.setTransactionId(numberGenerationService.generateTransactionId());
                    roomChargeTransaction.setFolioNo(checkIn.getFolioNo());
                    roomChargeTransaction.setRoomId(checkIn.getRoomId());
                    roomChargeTransaction.setGuestName(checkIn.getGuestName());
                    roomChargeTransaction.setDate(auditDate);
                    roomChargeTransaction.setAuditDate(auditDate);
                    roomChargeTransaction.setAmount(roomRate);
                    roomChargeTransaction.setNarration("Daily room charge for " + auditDate);
                    
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
                                     " (Room: " + room.getRoomNo() + ", Guest: " + checkIn.getGuestName() + ")");
                    
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
            System.out.println("- Failed Posts: " + failedPosts);
            
        } catch (Exception e) {
            System.err.println("Error during daily room charge posting: " + e.getMessage());
            e.printStackTrace();
        }
    }
}