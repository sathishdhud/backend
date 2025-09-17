package com.hotelworks.repository;

import com.hotelworks.entity.HotelAccountHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelAccountHeadRepository extends JpaRepository<HotelAccountHead, String> {
    
    Optional<HotelAccountHead> findByName(String name);
    
    boolean existsByName(String name);
}