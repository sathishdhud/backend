package com.hotelworks.service;

import com.hotelworks.entity.Refund;
import com.hotelworks.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RefundVoucherNumberTest {

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private NumberGenerationService numberGenerationService;

    @InjectMocks
    private RefundService refundService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateRefundVoucherNumber() {
        // Arrange
        when(refundRepository.count()).thenReturn(0L);

        // Act
        String voucherNumber = numberGenerationService.generateRefundVoucherNumber();

        // Assert
        assertNotNull(voucherNumber);
        assertTrue(voucherNumber.startsWith("RF"));
        assertTrue(voucherNumber.contains("-")); // Should contain accounting year format
    }

    @Test
    void testSequentialRefundVoucherNumbers() {
        // Arrange
        when(refundRepository.count()).thenReturn(0L, 1L, 2L);

        // Act
        String voucher1 = numberGenerationService.generateRefundVoucherNumber();
        String voucher2 = numberGenerationService.generateRefundVoucherNumber();
        String voucher3 = numberGenerationService.generateRefundVoucherNumber();

        // Assert
        assertTrue(voucher1.startsWith("RF1-"));
        assertTrue(voucher2.startsWith("RF2-"));
        assertTrue(voucher3.startsWith("RF3-"));
    }

    @Test
    void testCreateRefundGeneratesVoucherNumber() {
        // Arrange
        Refund refund = new Refund();
        refund.setRefundId("REF001");
        refund.setReceiptNo("R001");
        refund.setDate(LocalDate.now());
        refund.setModeOfPaymentId("CASH");
        refund.setAmount(BigDecimal.valueOf(100.00));
        refund.setNarration("Test refund");
        refund.setShiftNo("SHIFT001");
        refund.setShiftDate(LocalDate.now());
        refund.setBillNo("B001");
        refund.setFolioNo("F001");
        refund.setGuestName("Test Guest");

        Refund savedRefund = new Refund();
        savedRefund.setRefundId("REF001");
        savedRefund.setReceiptNo("R001");
        savedRefund.setVoucherNo("RF1-25-26");
        savedRefund.setDate(LocalDate.now());
        savedRefund.setModeOfPaymentId("CASH");
        savedRefund.setAmount(BigDecimal.valueOf(100.00));
        savedRefund.setNarration("Test refund");
        savedRefund.setShiftNo("SHIFT001");
        savedRefund.setShiftDate(LocalDate.now());
        savedRefund.setBillNo("B001");
        savedRefund.setFolioNo("F001");
        savedRefund.setGuestName("Test Guest");

        when(refundRepository.save(any(Refund.class))).thenReturn(savedRefund);

        // Act
        Refund result = refundService.createRefund(refund);

        // Assert
        assertNotNull(result);
        assertEquals("RF1-25-26", result.getVoucherNo());
        verify(refundRepository, times(1)).save(any(Refund.class));
    }
}