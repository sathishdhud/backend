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
    
    // Override default findAll to exclude deleted reservations
    @Query("SELECT r FROM Reservation r WHERE r.deleted = false")
    List<Reservation> findAll();
    
    @Query("SELECT r FROM Reservation r WHERE r.guestName = :guestName AND r.deleted = false")
    List<Reservation> findByGuestName(@Param("guestName") String guestName);
    
    @Query("SELECT r FROM Reservation r WHERE r.mobileNumber = :mobileNumber AND r.deleted = false")
    List<Reservation> findByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT r FROM Reservation r WHERE r.arrivalDate = :arrivalDate AND r.deleted = false")
    List<Reservation> findByArrivalDate(@Param("arrivalDate") LocalDate arrivalDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.departureDate = :departureDate AND r.deleted = false")
    List<Reservation> findByDepartureDate(@Param("departureDate") LocalDate departureDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.companyId = :companyId AND r.deleted = false")
    List<Reservation> findByCompanyId(@Param("companyId") String companyId);
    
    @Query("SELECT r FROM Reservation r WHERE r.roomTypeId = :roomTypeId AND r.deleted = false")
    List<Reservation> findByRoomTypeId(@Param("roomTypeId") String roomTypeId);
    
    @Query("SELECT r FROM Reservation r WHERE r.arrivalDate = :date AND r.deleted = false")
    List<Reservation> findExpectedArrivals(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.departureDate = :date AND r.deleted = false")
    List<Reservation> findExpectedDepartures(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.arrivalDate BETWEEN :startDate AND :endDate AND r.deleted = false")
    List<Reservation> findReservationsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.roomsCheckedIn < r.noOfRooms AND r.deleted = false")
    List<Reservation> findPendingCheckIns();
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "(LOWER(r.guestName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "r.mobileNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "r.reservationNo LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(r.emailId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND r.deleted = false")
    List<Reservation> searchReservations(@Param("searchTerm") String searchTerm);
    
    // Method to find deleted reservations
    @Query("SELECT r FROM Reservation r WHERE r.deleted = true")
    List<Reservation> findDeletedReservations();
    
    // Method to find a reservation by ID including deleted ones
    @Query("SELECT r FROM Reservation r WHERE r.reservationNo = :reservationNo")
    Optional<Reservation> findByReservationNoIncludingDeleted(@Param("reservationNo") String reservationNo);
}