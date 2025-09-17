package com.hotelworks.repository;

import com.hotelworks.entity.ArrivalMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArrivalModeRepository extends JpaRepository<ArrivalMode, String> {
    
    Optional<ArrivalMode> findByArrivalMode(String arrivalMode);
    
    boolean existsByArrivalMode(String arrivalMode);
}