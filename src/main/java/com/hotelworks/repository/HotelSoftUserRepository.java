package com.hotelworks.repository;

import com.hotelworks.entity.HotelSoftUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelSoftUserRepository extends JpaRepository<HotelSoftUser, String> {
    
    Optional<HotelSoftUser> findByUserName(String userName);
    
    @Query("SELECT h FROM HotelSoftUser h WHERE h.userTypeId = :userTypeId")
    List<HotelSoftUser> findByUserTypeId(@Param("userTypeId") String userTypeId);
    
    @Query("SELECT h FROM HotelSoftUser h WHERE h.userTypeRole = :userTypeRole")
    List<HotelSoftUser> findByUserTypeRole(@Param("userTypeRole") String userTypeRole);
    
    boolean existsByUserName(String userName);
}