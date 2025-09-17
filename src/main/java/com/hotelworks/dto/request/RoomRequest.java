package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomRequest {
    
    private String roomId; // Optional - auto-generated if not provided
    
    @NotBlank(message = "Room number is required")
    private String roomNo;
    
    @NotBlank(message = "Floor is required")
    private String floor;
    
    @NotBlank(message = "Status is required")
    private String status; // VR, OD, OI, Blocked
    
    private String roomTypeId;
    
    // Constructors
    public RoomRequest() {}
    
    public RoomRequest(String roomId, String roomNo, String floor, String status, String roomTypeId) {
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.floor = floor;
        this.status = status;
        this.roomTypeId = roomTypeId;
    }
    
    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
}