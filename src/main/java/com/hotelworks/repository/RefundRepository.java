package com.hotelworks.repository;

import com.hotelworks.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, String> {
    
    List<Refund> findByBillNo(String billNo);
    
    List<Refund> findByFolioNo(String folioNo);
    
    List<Refund> findByGuestName(String guestName);
    
    List<Refund> findByDate(LocalDate date);
    
    List<Refund> findByShiftNoAndShiftDate(String shiftNo, LocalDate shiftDate);
    
    // Method to get total refunds for a specific shift
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.shiftNo = :shiftNo AND r.shiftDate = :shiftDate")
    BigDecimal getTotalRefundsByShift(@Param("shiftNo") String shiftNo, @Param("shiftDate") LocalDate shiftDate);
    
    // Method to get total cash refunds for a specific shift
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.shiftNo = :shiftNo AND r.shiftDate = :shiftDate AND r.modeOfPaymentId = 'CASH'")
    BigDecimal getTotalCashRefundsByShift(@Param("shiftNo") String shiftNo, @Param("shiftDate") LocalDate shiftDate);
}