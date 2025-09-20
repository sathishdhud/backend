package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RoomShiftRequest {
    
    @NotBlank(message = "Current room ID is required")
    private String currentRoomId;
    
    @NotBlank(message = "New room ID is required")
    private String newRoomId;
    
    @NotNull(message = "Folio number is required")
    private String folioNo;
    
    private String remarks;
    
    // Constructors
    public RoomShiftRequest() {}
    
    public RoomShiftRequest(String currentRoomId, String newRoomId, String folioNo, String remarks) {
        this.currentRoomId = currentRoomId;
        this.newRoomId = newRoomId;
        this.folioNo = folioNo;
        this.remarks = remarks;
    }
    
    // Getters and Setters
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }
    
    public String getNewRoomId() {
        return newRoomId;
    }
    
    public void setNewRoomId(String newRoomId) {
        this.newRoomId = newRoomId;
    }
    
    public String getFolioNo() {
        return folioNo;
    }
    
    public void setFolioNo(String folioNo) {
        this.folioNo = folioNo;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}