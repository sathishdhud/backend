package com.hotelworks.repository;

import com.hotelworks.entity.SalesReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesReceiptRepository extends JpaRepository<SalesReceipt, String> {
    
    // Method to get sales receipts by shift information
    List<SalesReceipt> findByShiftNoAndShiftDate(String shiftNo, LocalDate shiftDate);
    
    // Method to get total cash sales for a specific shift
    @Query("SELECT SUM(s.amount) FROM SalesReceipt s WHERE s.shiftNo = :shiftNo AND s.shiftDate = :shiftDate AND s.modeOfPaymentId = 'CASH'")
    BigDecimal getTotalCashSalesByShift(@Param("shiftNo") String shiftNo, @Param("shiftDate") LocalDate shiftDate);
}