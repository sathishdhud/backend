package com.hotelworks.service;

import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.PostTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VoucherNumberGenerationTest {

    @Mock
    private PostTransactionRepository postTransactionRepository;

    @InjectMocks
    private NumberGenerationService numberGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateExpenseVoucherNumber() {
        // Arrange
        when(postTransactionRepository.count()).thenReturn(0L);

        // Act
        String voucherNumber = numberGenerationService.generateExpenseVoucherNumber();

        // Assert
        assertNotNull(voucherNumber);
        assertTrue(voucherNumber.startsWith("E"));
        assertTrue(voucherNumber.contains("-")); // Should contain accounting year format
    }

    @Test
    void testGenerateTransactionVoucherNumber() {
        // Arrange
        when(postTransactionRepository.count()).thenReturn(0L);

        // Act
        String voucherNumber = numberGenerationService.generateTransactionVoucherNumber();

        // Assert
        assertNotNull(voucherNumber);
        assertTrue(voucherNumber.startsWith("T"));
        assertTrue(voucherNumber.contains("-")); // Should contain accounting year format
    }

    @Test
    void testSequentialVoucherNumbers() {
        // Arrange
        when(postTransactionRepository.count()).thenReturn(0L, 1L, 2L);

        // Act
        String voucher1 = numberGenerationService.generateExpenseVoucherNumber();
        String voucher2 = numberGenerationService.generateExpenseVoucherNumber();
        String voucher3 = numberGenerationService.generateExpenseVoucherNumber();

        // Assert
        assertEquals("E1-", voucher1.substring(0, Math.min(voucher1.length(), 3)));
        assertEquals("E2-", voucher2.substring(0, Math.min(voucher2.length(), 3)));
        assertEquals("E3-", voucher3.substring(0, Math.min(voucher3.length(), 3)));
    }
}