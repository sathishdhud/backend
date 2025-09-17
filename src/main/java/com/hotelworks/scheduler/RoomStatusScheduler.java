package com.hotelworks.scheduler;

import com.hotelworks.service.RoomStatusManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job for automatic room status management
 */
@Component
public class RoomStatusScheduler {
    
    @Autowired
    private RoomStatusManagementService roomStatusManagementService;
    
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
}