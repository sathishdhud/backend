package com.hotelworks.repository;

import com.hotelworks.entity.DeletedReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletedReservationRepository extends JpaRepository<DeletedReservation, String> {
    
    List<DeletedReservation> findByGuestName(String guestName);
    
    List<DeletedReservation> findByMobileNumber(String mobileNumber);
}