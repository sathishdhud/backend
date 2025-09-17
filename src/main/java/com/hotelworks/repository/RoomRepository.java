package com.hotelworks.repository;

import com.hotelworks.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    
    Optional<Room> findByRoomNo(String roomNo);
    
    List<Room> findByStatus(String status);
    
    List<Room> findByFloor(String floor);
    
    @Query("SELECT r FROM Room r WHERE r.roomTypeId = :roomTypeId")
    List<Room> findByRoomTypeId(@Param("roomTypeId") String roomTypeId);
    
    @Query("SELECT r FROM Room r WHERE r.status = 'VR'")
    List<Room> findAvailableRooms();
    
    @Query("SELECT r FROM Room r WHERE r.status = 'OD'")
    List<Room> findOccupiedRooms();
    
    @Query("SELECT r FROM Room r WHERE r.floor = :floor AND r.status = :status")
    List<Room> findByFloorAndStatus(@Param("floor") String floor, @Param("status") String status);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    Long countByStatus(@Param("status") String status);
}