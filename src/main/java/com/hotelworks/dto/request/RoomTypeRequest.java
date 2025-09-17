package com.hotelworks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class RoomTypeRequest {
    
    // typeId is now optional - will be auto-generated if not provided
    private String typeId;
    
    @NotBlank(message = "Type name is required")
    private String typeName;
    
    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Number of rooms must be at least 1")
    private Integer noOfRooms;
    
    // Constructors
    public RoomTypeRequest() {}
    
    public RoomTypeRequest(String typeId, String typeName, Integer noOfRooms) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.noOfRooms = noOfRooms;
    }
    
    // Getters and Setters
    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    public Integer getNoOfRooms() { return noOfRooms; }
    public void setNoOfRooms(Integer noOfRooms) { this.noOfRooms = noOfRooms; }
}