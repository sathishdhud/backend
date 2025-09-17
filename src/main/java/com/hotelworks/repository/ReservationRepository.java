package com.hotelworks.repository;

import com.hotelworks.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    
    List<Reservation> findByGuestName(String guestName);
    
    List<Reservation> findByMobileNumber(String mobileNumber);
    
    List<Reservation> findByArrivalDate(LocalDate arrivalDate);
    
    List<Reservation> findByDepartureDate(LocalDate departureDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.companyId = :companyId")
    List<Reservation> findByCompanyId(@Param("companyId") String companyId);
    
    @Query("SELECT r FROM Reservation r WHERE r.roomTypeId = :roomTypeId")
    List<Reservation> findByRoomTypeId(@Param("roomTypeId") String roomTypeId);
    
    @Query("SELECT r FROM Reservation r WHERE r.arrivalDate = :date")
    List<Reservation> findExpectedArrivals(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.departureDate = :date")
    List<Reservation> findExpectedDepartures(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.arrivalDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.roomsCheckedIn < r.noOfRooms")
    List<Reservation> findPendingCheckIns();
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "LOWER(r.guestName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "r.mobileNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "r.reservationNo LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(r.emailId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Reservation> searchReservations(@Param("searchTerm") String searchTerm);
}