package com.hotelworks.repository;

import com.hotelworks.entity.BillSettlementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillSettlementTypeRepository extends JpaRepository<BillSettlementType, String> {
    
    Optional<BillSettlementType> findByName(String name);
    
    boolean existsByName(String name);
}