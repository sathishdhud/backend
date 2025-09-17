package com.hotelworks.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class FoBillTest {
    
    private FoBill bill;
    
    @BeforeEach
    public void setUp() {
        bill = new FoBill();
    }
    
    @Test
    public void testCalculateBalanceAmount_NormalCase() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("30.00"));
        bill.calculateBalanceAmount();
        assertEquals(new BigDecimal("70.00"), bill.getBalanceAmount());
    }
    
    @Test
    public void testCalculateBalanceAmount_ZeroBalance() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("100.00"));
        bill.calculateBalanceAmount();
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
    }
    
    @Test
    public void testCalculateBalanceAmount_Overpayment() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("150.00"));
        bill.calculateBalanceAmount();
        // Balance should be zero, not negative
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
    }
    
    @Test
    public void testUpdateSettlementStatus_Overpayment() {
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("150.00"));
        bill.calculateBalanceAmount();
        bill.updateSettlementStatus();
        // Should be marked as SETTLED when balance is zero or negative
        assertEquals("SETTLED", bill.getSettlementStatus());
    }
}