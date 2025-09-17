package com.hotelworks.dto.response;

public class RoomTypeResponse {
    
    private String typeId;
    private String typeName;
    private Integer noOfRooms;
    
    // Constructors
    public RoomTypeResponse() {}
    
    public RoomTypeResponse(String typeId, String typeName, Integer noOfRooms) {
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