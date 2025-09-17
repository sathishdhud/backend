package com.hotelworks.repository;

import com.hotelworks.entity.FoBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoBillRepository extends JpaRepository<FoBill, String> {
    
    Optional<FoBill> findByFolioNo(String folioNo);
    
    List<FoBill> findByGuestName(String guestName);
    
    List<FoBill> findByRoomId(String roomId);
    
    @Query("SELECT f FROM FoBill f WHERE f.generatedAt BETWEEN :startDate AND :endDate")
    List<FoBill> findBillsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(f.totalAmount) FROM FoBill f WHERE f.generatedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT f FROM FoBill f WHERE f.guestName LIKE %:searchTerm% OR f.billNo LIKE %:searchTerm% OR f.folioNo LIKE %:searchTerm%")
    List<FoBill> searchBills(@Param("searchTerm") String searchTerm);
    
    // Split bill related queries
    @Query("SELECT COUNT(f) FROM FoBill f WHERE f.originalBillNo = :originalBillNo AND f.isSplitBill = true")
    Long countSplitsByOriginalBill(@Param("originalBillNo") String originalBillNo);
    
    @Query("SELECT f FROM FoBill f WHERE f.originalBillNo = :originalBillNo ORDER BY f.splitSequence")
    List<FoBill> findSplitBillsByOriginalBill(@Param("originalBillNo") String originalBillNo);
    
    // Find bills by settlement status for deferred payment tracking
    List<FoBill> findBySettlementStatusIn(List<String> statuses);
    
    // Find all bills related to an original bill (including the original)
    @Query("SELECT f FROM FoBill f WHERE f.billNo = :billNo OR f.originalBillNo = :billNo ORDER BY f.generatedAt")
    List<FoBill> findRelatedBills(@Param("billNo") String billNo);
}