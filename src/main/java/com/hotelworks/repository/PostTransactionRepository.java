package com.hotelworks.repository;

import com.hotelworks.entity.PostTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostTransactionRepository extends JpaRepository<PostTransaction, String> {
    
    List<PostTransaction> findByFolioNo(String folioNo);
    
    List<PostTransaction> findByBillNo(String billNo);
    
    List<PostTransaction> findByRoomId(String roomId);
    
    List<PostTransaction> findByGuestName(String guestName);
    
    List<PostTransaction> findByDate(LocalDate date);
    
    @Query("SELECT p FROM PostTransaction p WHERE p.accHeadId = :accHeadId")
    List<PostTransaction> findByAccHeadId(@Param("accHeadId") String accHeadId);
    
    @Query("SELECT SUM(p.amount) FROM PostTransaction p WHERE p.folioNo = :folioNo")
    BigDecimal getTotalTransactionsByFolio(@Param("folioNo") String folioNo);
    
    @Query("SELECT SUM(p.amount) FROM PostTransaction p WHERE p.billNo = :billNo")
    BigDecimal getTotalTransactionsByBill(@Param("billNo") String billNo);
    
    @Query("SELECT p FROM PostTransaction p WHERE p.date BETWEEN :startDate AND :endDate")
    List<PostTransaction> findTransactionsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.amount) FROM PostTransaction p WHERE p.accHeadId = :accHeadId AND p.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalByAccountHeadAndDateRange(@Param("accHeadId") String accHeadId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM PostTransaction p WHERE p.folioNo = :folioNo ORDER BY p.date DESC")
    List<PostTransaction> findByFolioNoOrderByDateDesc(@Param("folioNo") String folioNo);
}