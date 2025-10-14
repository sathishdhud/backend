package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.PostTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class ExpenseServiceTransactionTest {

    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private PostTransactionRepository postTransactionRepository;

    @MockBean
    private NumberGenerationService numberGenerationService;

    @Test
    public void testCreateExpense_SavesToPostTransaction() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setVoucherNo("V001");
        request.setDate(LocalDate.now());
        request.setAccountHeadId("ACC001");
        request.setAmount(new BigDecimal("100.00"));
        request.setNarration("Test expense");
        request.setShiftNo("S001");
        request.setShiftDate(LocalDate.now());

        PostTransaction savedTransaction = new PostTransaction();
        savedTransaction.setTransactionId("TXN001");
        savedTransaction.setVoucherNo("V001");
        savedTransaction.setDate(LocalDate.now());
        savedTransaction.setAccHeadId("ACC001");
        savedTransaction.setAmount(new BigDecimal("100.00"));
        savedTransaction.setNarration("Test expense");
        savedTransaction.setShiftNo("S001");
        savedTransaction.setShiftDate(LocalDate.now());
        savedTransaction.setGuestName("Unknown Guest");

        when(numberGenerationService.generateTransactionId()).thenReturn("TXN001");
        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("TXN001", response.getTransactionId());
        assertEquals("V001", response.getVoucherNo());
        assertEquals("ACC001", response.getAccountHeadId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());

        // Verify that save was called on PostTransactionRepository
        verify(postTransactionRepository, times(1)).save(any(PostTransaction.class));
    }
}