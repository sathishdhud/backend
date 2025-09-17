package com.hotelworks.repository;

import com.hotelworks.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    
    List<Shift> findByShiftDate(LocalDate shiftDate);
    
    Optional<Shift> findByShiftNoAndShiftDate(String shiftNo, LocalDate shiftDate);
    
    @Query("SELECT s FROM Shift s WHERE s.shiftDate BETWEEN :startDate AND :endDate ORDER BY s.shiftDate DESC")
    List<Shift> findShiftsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Shift s WHERE s.shiftDate = :date ORDER BY s.shiftNo")
    List<Shift> findShiftsByDateOrderByShiftNo(@Param("date") LocalDate date);
}