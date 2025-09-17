package com.hotelworks.service;

import com.hotelworks.entity.FoBill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillServiceTest {
    
    private BillService billService;
    
    @BeforeEach
    public void setUp() {
        billService = new BillService();
    }
    
    @Test
    public void testCalculateCurrentBalance_NormalCase() {
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("30.00"));
        
        // Using reflection to access private method
        try {
            java.lang.reflect.Method method = BillService.class.getDeclaredMethod("calculateCurrentBalance", FoBill.class);
            method.setAccessible(true);
            BigDecimal balance = (BigDecimal) method.invoke(billService, bill);
            assertEquals(new BigDecimal("70.00"), balance);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testCalculateCurrentBalance_ZeroBalance() {
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("100.00"));
        
        // Using reflection to access private method
        try {
            java.lang.reflect.Method method = BillService.class.getDeclaredMethod("calculateCurrentBalance", FoBill.class);
            method.setAccessible(true);
            BigDecimal balance = (BigDecimal) method.invoke(billService, bill);
            assertEquals(BigDecimal.ZERO, balance);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testCalculateCurrentBalance_Overpayment() {
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("150.00"));
        
        // Using reflection to access private method
        try {
            java.lang.reflect.Method method = BillService.class.getDeclaredMethod("calculateCurrentBalance", FoBill.class);
            method.setAccessible(true);
            BigDecimal balance = (BigDecimal) method.invoke(billService, bill);
            // Balance should be zero, not negative
            assertEquals(BigDecimal.ZERO, balance);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}