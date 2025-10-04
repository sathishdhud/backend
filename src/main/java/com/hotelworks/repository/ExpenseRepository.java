package com.hotelworks.repository;

import com.hotelworks.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {
    
    List<Expense> findByVoucherNo(String voucherNo);
    
    List<Expense> findByAccountHeadId(String accountHeadId);
    
    List<Expense> findByDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
    
    List<Expense> findByShiftNo(String shiftNo);
}