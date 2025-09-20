package com.hotelworks.repository;

import com.hotelworks.entity.Hmsystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HmsystemRepository extends JpaRepository<Hmsystem, Long> {
    
    @Query("SELECT h FROM Hmsystem h ORDER BY h.id DESC LIMIT 1")
    Optional<Hmsystem> findLatestRecord();
    
    List<Hmsystem> findAllByOrderByCreatedAtDesc();
}