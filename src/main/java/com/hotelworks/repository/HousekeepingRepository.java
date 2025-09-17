package com.hotelworks.repository;

import com.hotelworks.entity.Housekeeping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {
    
    List<Housekeeping> findByRoomId(String roomId);
    
    List<Housekeeping> findByStatus(String status);
    
    List<Housekeeping> findByAssignedTo(String assignedTo);
    
    List<Housekeeping> findByRoomIdAndStatus(String roomId, String status);
}