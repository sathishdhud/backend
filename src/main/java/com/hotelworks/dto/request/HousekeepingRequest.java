package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HousekeepingRequest {
    
    @NotBlank(message = "Room ID is required")
    private String roomId;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String notes;
    
    private String assignedTo;
    
    // Constructors
    public HousekeepingRequest() {}
    
    public HousekeepingRequest(String roomId, String status) {
        this.roomId = roomId;
        this.status = status;
    }
    
    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}