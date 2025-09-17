package com.hotelworks.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class FoBillBalanceTest {
    
    private FoBill bill;
    
    @BeforeEach
    public void setUp() {
        bill = new FoBill();
    }
    
    @Test
    public void testNormalBalanceCalculation() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("30.00"));
        
        assertEquals(new BigDecimal("70.00"), bill.getBalanceAmount());
        assertEquals("PARTIAL", bill.getSettlementStatus());
    }
    
    @Test
    public void testZeroBalanceCalculation() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("100.00"));
        
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
    
    @Test
    public void testOverpaymentBalanceCalculation() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("150.00"));
        
        // Balance should be zero, not negative
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
    
    @Test
    public void testOrderIndependence() {
        // Set paid amount first, then total amount
        bill.setPaidAmount(new BigDecimal("200.00"));
        bill.setTotalAmount(new BigDecimal("50.00"));
        
        // Balance should still be zero
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
    
    @Test
    public void testNullValues() {
        // Test with null values
        bill.setTotalAmount(null);
        bill.setPaidAmount(null);
        
        // Should not throw exception and balance should be zero
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
    }
    
    @Test
    public void testSettingBalanceDirectly() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("50.00"));
        
        // Verify initial state
        assertEquals(new BigDecimal("50.00"), bill.getBalanceAmount());
        assertEquals("PARTIAL", bill.getSettlementStatus());
        
        // Set balance directly to zero
        bill.setBalanceAmount(BigDecimal.ZERO);
        
        // Settlement status should update to SETTLED
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
}