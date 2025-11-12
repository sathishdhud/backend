package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExpenseServiceVoucherNumberTest {

    @Mock
    private PostTransactionRepository postTransactionRepository;

    @Mock
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Mock
    private NumberGenerationService numberGenerationService;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExpenseGeneratesSequentialVoucherNumber() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setDate(LocalDate.now());
        request.setAccountHeadId("ACC001");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setNarration("Test expense");
        request.setShiftNo("SHIFT001");
        request.setShiftDate(LocalDate.now());

        // Mock the account head validation
        when(hotelAccountHeadRepository.existsById("ACC001")).thenReturn(true);

        // Mock the number generation service
        when(numberGenerationService.generateTransactionId()).thenReturn("TXN12345");
        when(numberGenerationService.generateExpenseVoucherNumber()).thenReturn("E1-25-26");

        // Mock the saved transaction
        PostTransaction savedTransaction = new PostTransaction();
        savedTransaction.setTransactionId("TXN12345");
        savedTransaction.setVoucherNo("E1-25-26");
        savedTransaction.setDate(LocalDate.now());
        savedTransaction.setAccHeadId("ACC001");
        savedTransaction.setAmount(BigDecimal.valueOf(100.00));
        savedTransaction.setNarration("Test expense");
        savedTransaction.setShiftNo("SHIFT001");
        savedTransaction.setShiftDate(LocalDate.now());
        savedTransaction.setGuestName("Unknown Guest");

        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("E1-25-26", response.getVoucherNo());
        assertEquals("TXN12345", response.getTransactionId());
        verify(numberGenerationService, times(1)).generateExpenseVoucherNumber();
        verify(postTransactionRepository, times(1)).save(any(PostTransaction.class));
    }

    @Test
    void testSequentialVoucherNumbers() {
        // Arrange
        ExpenseRequest request1 = new ExpenseRequest();
        request1.setDate(LocalDate.now());
        request1.setAccountHeadId("ACC001");
        request1.setAmount(BigDecimal.valueOf(100.00));
        request1.setNarration("Test expense 1");
        request1.setShiftNo("SHIFT001");
        request1.setShiftDate(LocalDate.now());

        ExpenseRequest request2 = new ExpenseRequest();
        request2.setDate(LocalDate.now());
        request2.setAccountHeadId("ACC002");
        request2.setAmount(BigDecimal.valueOf(200.00));
        request2.setNarration("Test expense 2");
        request2.setShiftNo("SHIFT001");
        request2.setShiftDate(LocalDate.now());

        // Mock the account head validation
        when(hotelAccountHeadRepository.existsById("ACC001")).thenReturn(true);
        when(hotelAccountHeadRepository.existsById("ACC002")).thenReturn(true);

        // Mock the number generation service
        when(numberGenerationService.generateTransactionId()).thenReturn("TXN12345", "TXN12346");
        when(numberGenerationService.generateExpenseVoucherNumber()).thenReturn("E1-25-26", "E2-25-26");

        // Mock the saved transactions
        PostTransaction savedTransaction1 = new PostTransaction();
        savedTransaction1.setTransactionId("TXN12345");
        savedTransaction1.setVoucherNo("E1-25-26");
        savedTransaction1.setDate(LocalDate.now());
        savedTransaction1.setAccHeadId("ACC001");
        savedTransaction1.setAmount(BigDecimal.valueOf(100.00));
        savedTransaction1.setNarration("Test expense 1");
        savedTransaction1.setShiftNo("SHIFT001");
        savedTransaction1.setShiftDate(LocalDate.now());
        savedTransaction1.setGuestName("Unknown Guest");

        PostTransaction savedTransaction2 = new PostTransaction();
        savedTransaction2.setTransactionId("TXN12346");
        savedTransaction2.setVoucherNo("E2-25-26");
        savedTransaction2.setDate(LocalDate.now());
        savedTransaction2.setAccHeadId("ACC002");
        savedTransaction2.setAmount(BigDecimal.valueOf(200.00));
        savedTransaction2.setNarration("Test expense 2");
        savedTransaction2.setShiftNo("SHIFT001");
        savedTransaction2.setShiftDate(LocalDate.now());
        savedTransaction2.setGuestName("Unknown Guest");

        when(postTransactionRepository.save(any(PostTransaction.class)))
            .thenReturn(savedTransaction1)
            .thenReturn(savedTransaction2);

        // Act
        ExpenseResponse response1 = expenseService.createExpense(request1);
        ExpenseResponse response2 = expenseService.createExpense(request2);

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals("E1-25-26", response1.getVoucherNo());
        assertEquals("E2-25-26", response2.getVoucherNo());
        verify(numberGenerationService, times(2)).generateExpenseVoucherNumber();
        verify(postTransactionRepository, times(2)).save(any(PostTransaction.class));
    }
}