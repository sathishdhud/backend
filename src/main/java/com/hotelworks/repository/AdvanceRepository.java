package com.hotelworks.repository;

import com.hotelworks.entity.Advance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, String> {
    
    List<Advance> findByReservationNo(String reservationNo);
    
    List<Advance> findByFolioNo(String folioNo);
    
    List<Advance> findByBillNo(String billNo);
    
    List<Advance> findByGuestName(String guestName);
    
    List<Advance> findByDate(LocalDate date);
    
    List<Advance> findByShiftNo(String shiftNo);
    
    // New methods for reservation with bill or folio
    List<Advance> findByReservationNoAndBillNo(String reservationNo, String billNo);
    
    List<Advance> findByReservationNoAndFolioNo(String reservationNo, String folioNo);
    
    @Query("SELECT a FROM Advance a WHERE a.modeOfPaymentId = :modeOfPaymentId")
    List<Advance> findByModeOfPaymentId(@Param("modeOfPaymentId") String modeOfPaymentId);
    
    @Query("SELECT SUM(a.amount) FROM Advance a WHERE a.reservationNo = :reservationNo")
    BigDecimal getTotalAdvancesByReservation(@Param("reservationNo") String reservationNo);
    
    @Query("SELECT SUM(a.amount) FROM Advance a WHERE a.folioNo = :folioNo")
    BigDecimal getTotalAdvancesByFolio(@Param("folioNo") String folioNo);
    
    @Query("SELECT SUM(a.amount) FROM Advance a WHERE a.billNo = :billNo")
    BigDecimal getTotalAdvancesByBill(@Param("billNo") String billNo);
    
    @Query("SELECT a FROM Advance a WHERE a.date BETWEEN :startDate AND :endDate")
    List<Advance> findAdvancesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(a.amount) FROM Advance a WHERE a.shiftNo = :shiftNo")
    BigDecimal getTotalAdvancesByShift(@Param("shiftNo") String shiftNo);
}