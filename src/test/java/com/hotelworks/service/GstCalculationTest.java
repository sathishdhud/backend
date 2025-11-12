package com.hotelworks.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GstCalculationTest {

    @Test
    public void testInclusiveGstCalculation() {
        // Test case: Room rate is 5000 and includes 10% GST (5% CGST + 5% SGST)
        // Expected result: Base room rate = 4545, CGST = 227, SGST = 227, Total = 5000
        
        BigDecimal roomRate = new BigDecimal("5000");
        BigDecimal divisor = BigDecimal.valueOf(1.10);
        
        // Calculate base room rate
        BigDecimal baseRoomRate = roomRate.divide(divisor, 2, RoundingMode.HALF_UP);
        baseRoomRate = baseRoomRate.setScale(0, RoundingMode.HALF_UP); // Round to 0 decimal places
        
        // Calculate tax amounts (5% each)
        BigDecimal cgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal sgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
        
        // Adjust for rounding differences
        BigDecimal totalCalculated = baseRoomRate.add(cgstAmount).add(sgstAmount);
        BigDecimal difference = roomRate.subtract(totalCalculated);
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            // Add the difference to base room rate to maintain total
            baseRoomRate = baseRoomRate.add(difference);
        }
        
        // Recalculate taxes with adjusted base room rate
        cgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
        sgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
        
        BigDecimal total = baseRoomRate.add(cgstAmount).add(sgstAmount);
        
        // Assertions
        assertEquals(new BigDecimal("4545"), baseRoomRate);
        assertEquals(new BigDecimal("227"), cgstAmount);
        assertEquals(new BigDecimal("227"), sgstAmount);
        assertEquals(roomRate, total);
    }
    
    @Test
    public void testExclusiveGstCalculation() {
        // Test case: Room rate is 5000 and does not include GST
        // Expected result: Base room rate = 5000, CGST = 125, SGST = 125, Total = 5250
        
        BigDecimal roomRate = new BigDecimal("5000");
        BigDecimal baseRoomRate = roomRate;
        
        // Calculate tax amounts (2.5% each for a total of 5% GST)
        BigDecimal cgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.025)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal sgstAmount = baseRoomRate.multiply(BigDecimal.valueOf(0.025)).setScale(0, RoundingMode.HALF_UP);
        
        BigDecimal total = baseRoomRate.add(cgstAmount).add(sgstAmount);
        
        // Assertions
        assertEquals(new BigDecimal("5000"), baseRoomRate);
        assertEquals(new BigDecimal("125"), cgstAmount);
        assertEquals(new BigDecimal("125"), sgstAmount);
        assertEquals(new BigDecimal("5250"), total);
    }
}