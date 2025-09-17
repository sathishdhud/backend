package com.hotelworks.repository;

import com.hotelworks.entity.Taxation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxationRepository extends JpaRepository<Taxation, String> {
    
    Optional<Taxation> findByTaxName(String taxName);
    
    boolean existsByTaxName(String taxName);
}