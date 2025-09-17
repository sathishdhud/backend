package com.hotelworks.repository;

import com.hotelworks.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, String> {
    
    Optional<RoomType> findByTypeName(String typeName);
    
    boolean existsByTypeName(String typeName);
}