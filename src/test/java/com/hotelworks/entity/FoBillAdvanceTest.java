package com.hotelworks.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FoBillAdvanceTest {

    @Test
    public void testBalanceCalculationWithAdvances() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00"));
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("800.00"));
        bill.setAdvanceAmount(new BigDecimal("300.00")); // Total advances
        bill.setPaidAmount(new BigDecimal("100.00")); // Only post-check-in advances
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // With reservation rate of 1000 and advances of 300, balance should be 700
        assertEquals(new BigDecimal("700.00"), bill.getBalanceAmount());
    }
    
    @Test
    public void testBalanceCalculationWithoutReservation() {
        // Create test objects without reservation
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("800.00"));
        bill.setAdvanceAmount(new BigDecimal("300.00"));
        bill.setPaidAmount(new BigDecimal("100.00")); // Only post-check-in advances
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Without reservation, should use total amount minus paid amount: 800 - 100 = 700
        assertEquals(new BigDecimal("700.00"), bill.getBalanceAmount());
    }
    
    @Test
    public void testSettlementStatusWithAdvances() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00"));
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("800.00"));
        bill.setAdvanceAmount(new BigDecimal("1000.00")); // Advances exceed rate
        bill.setPaidAmount(new BigDecimal("100.00")); // Only post-check-in advances
        
        // Calculate balance and update settlement status
        bill.calculateBalanceAmount();
        bill.updateSettlementStatus();
        
        // With advances exceeding rate, balance should be 0 and status should be SETTLED
        assertEquals(BigDecimal.ZERO.compareTo(bill.getBalanceAmount()), 0);
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
}