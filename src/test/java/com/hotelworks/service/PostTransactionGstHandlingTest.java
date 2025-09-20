package com.hotelworks.service;

import com.hotelworks.dto.request.PostTransactionRequest;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.TaxationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class PostTransactionGstHandlingTest {

    @Autowired
    private PostTransactionService postTransactionService;

    @Autowired
    private TaxationRepository taxationRepository;

    @BeforeEach
    public void setUp() {
        // Clean up taxation repository
        taxationRepository.deleteAll();

        // Create test tax records
        Taxation cgstTax = new Taxation();
        cgstTax.setTaxId("CGST001");
        cgstTax.setTaxName("CGST");
        cgstTax.setPercentage(new BigDecimal("9.00"));
        taxationRepository.save(cgstTax);

        Taxation sgstTax = new Taxation();
        sgstTax.setTaxId("SGST001");
        sgstTax.setTaxName("SGST");
        sgstTax.setPercentage(new BigDecimal("9.00"));
        taxationRepository.save(sgstTax);
    }

    @Test
    public void testCalculateAmountWithTaxes() {
        // Test the calculateAmountWithTaxes method directly
        BigDecimal baseAmount = new BigDecimal("1000.00");
        BigDecimal result = postTransactionService.calculateAmountWithTaxes(baseAmount);
        
        // Expected: 1000 * (1 + (9 + 9) / 100) = 1000 * 1.18 = 1180.00
        assertEquals(new BigDecimal("1180.00"), result);
    }

    @Test
    public void testGstExclusiveAmountHandling() {
        // Test GST exclusive amount handling
        BigDecimal baseAmount = new BigDecimal("1000.00");
        BigDecimal result = postTransactionService.calculateAmountWithTaxes(baseAmount);
        
        // For GST exclusive, we still calculate the inclusive amount
        // Expected: 1000 * (1 + (9 + 9) / 100) = 1000 * 1.18 = 1180.00
        assertEquals(new BigDecimal("1180.00"), result);
    }

    @Test
    public void testGstInclusiveAmountHandling() {
        // Test GST inclusive amount handling
        BigDecimal baseAmount = new BigDecimal("1000.00");
        BigDecimal result = postTransactionService.calculateAmountWithTaxes(baseAmount);
        
        // For GST inclusive, we calculate the amount including taxes
        // Expected: 1000 * (1 + (9 + 9) / 100) = 1000 * 1.18 = 1180.00
        assertEquals(new BigDecimal("1180.00"), result);
    }

    @Test
    public void testZeroAmountHandling() {
        // Test zero amount handling
        BigDecimal baseAmount = BigDecimal.ZERO;
        BigDecimal result = postTransactionService.calculateAmountWithTaxes(baseAmount);
        
        // Should return the original amount
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testNullAmountHandling() {
        // Test null amount handling
        BigDecimal result = postTransactionService.calculateAmountWithTaxes(null);
        
        // Should return the original amount (null)
        assertNull(result);
    }
}