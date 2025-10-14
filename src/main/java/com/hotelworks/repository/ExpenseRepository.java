package com.hotelworks.repository;

import com.hotelworks.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {
    
    List<Expense> findByVoucherNo(String voucherNo);
    
    List<Expense> findByAccountHeadId(String accountHeadId);
    
    List<Expense> findByDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
    
    List<Expense> findByShiftNo(String shiftNo);
    
    // Added method to find by voucher number (same as existing but with explicit name)
    @Query("SELECT e FROM Expense e WHERE e.voucherNo = :voucherNo")
    List<Expense> findByVoucherNo(@Param("voucherNo") String voucherNo);
}