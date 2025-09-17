package com.hotelworks.repository;

import com.hotelworks.entity.ResvSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResvSourceRepository extends JpaRepository<ResvSource, String> {
    
    Optional<ResvSource> findByResvSource(String resvSource);
    
    boolean existsByResvSource(String resvSource);
}