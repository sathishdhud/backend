package com.hotelworks.repository;

import com.hotelworks.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, String> {
    
    Optional<CheckIn> findByReservationNo(String reservationNo);
    
    List<CheckIn> findByGuestName(String guestName);
    
    List<CheckIn> findByRoomId(String roomId);
    
    List<CheckIn> findByArrivalDate(LocalDate arrivalDate);
    
    List<CheckIn> findByDepartureDate(LocalDate departureDate);
    
    @Query("SELECT c FROM CheckIn c WHERE c.walkIn = :walkIn")
    List<CheckIn> findByWalkInStatus(@Param("walkIn") String walkIn);
    
    @Query("SELECT c FROM CheckIn c WHERE c.departureDate = :date")
    List<CheckIn> findExpectedCheckouts(@Param("date") LocalDate date);
    
    @Query("SELECT c FROM CheckIn c WHERE c.arrivalDate <= :currentDate AND c.departureDate >= :currentDate")
    List<CheckIn> findInHouseGuests(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT c FROM CheckIn c WHERE c.guestName LIKE %:searchTerm% OR c.folioNo LIKE %:searchTerm% OR c.roomId LIKE %:searchTerm%")
    List<CheckIn> searchCheckIns(@Param("searchTerm") String searchTerm);
    
    boolean existsByRoomId(String roomId);
    
    // Find guests who should have checked out before today but rooms are still occupied
    @Query("SELECT c FROM CheckIn c WHERE c.departureDate < :currentDate")
    List<CheckIn> findOverdueCheckouts(@Param("currentDate") LocalDate currentDate);
    
    // Find overlapping stays for room availability checking
    @Query("SELECT c FROM CheckIn c WHERE c.roomId = :roomId AND " +
           "((c.arrivalDate <= :departureDate AND c.departureDate >= :arrivalDate))")
    List<CheckIn> findOverlappingStays(@Param("roomId") String roomId, 
                                       @Param("arrivalDate") LocalDate arrivalDate, 
                                       @Param("departureDate") LocalDate departureDate);
}