package com.hotelworks.repository;

import com.hotelworks.entity.RefMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefModeRepository extends JpaRepository<RefMode, String> {
    
    Optional<RefMode> findByRefMode(String refMode);
    
    boolean existsByRefMode(String refMode);
}