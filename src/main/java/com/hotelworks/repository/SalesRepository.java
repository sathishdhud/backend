package com.hotelworks.repository;

import com.hotelworks.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, String> {
    
    List<Sales> findByReceiptNumber(String receiptNumber);
    
    List<Sales> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Sales> findByModeOfPayment(String modeOfPayment);
    
    List<Sales> findByVoucherNumber(String voucherNumber);
    
    @Query("SELECT s FROM Sales s WHERE s.date = :date AND s.modeOfPayment = :modeOfPayment")
    List<Sales> findByDateAndModeOfPayment(@Param("date") LocalDate date, @Param("modeOfPayment") String modeOfPayment);
    
    @Query("SELECT SUM(s.amount) FROM Sales s WHERE s.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}