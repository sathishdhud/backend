package com.hotelworks.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FoBillTransactionTest {

    @Test
    public void testBalanceCalculationWithReservationAndAdditionalTransactions() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00")); // Room charges
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("1000.00")); // Additional transactions
        bill.setAdvanceAmount(new BigDecimal("5000.00")); // Advance paid
        bill.setPaidAmount(new BigDecimal("4000.00")); // Paid amount
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Expected calculation:
        // Total billable = Room charges (1000) + Additional transactions (1000) = 2000
        // Balance = Total billable (2000) - Advances (5000) = -3000, but should be 0 (can't be negative)
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
    }
    
    @Test
    public void testBalanceCalculationWithPartialPayment() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00")); // Room charges
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("1000.00")); // Additional transactions
        bill.setAdvanceAmount(new BigDecimal("1000.00")); // Advance paid
        bill.setPaidAmount(new BigDecimal("2000.00")); // Paid amount (should be capped)
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Expected calculation:
        // Total billable = Room charges (1000) + Additional transactions (1000) = 2000
        // Balance = Total billable (2000) - Advances (1000) = 1000
        assertEquals(new BigDecimal("1000.00"), bill.getBalanceAmount());
    }
    
    @Test
    public void testBalanceCalculationWithoutReservation() {
        // Create test objects without reservation
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("2000.00")); // Total transactions
        bill.setPaidAmount(new BigDecimal("1500.00")); // Paid amount
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Expected calculation:
        // Balance = Total transactions (2000) - Paid amount (1500) = 500
        assertEquals(new BigDecimal("500.00"), bill.getBalanceAmount());
    }
}