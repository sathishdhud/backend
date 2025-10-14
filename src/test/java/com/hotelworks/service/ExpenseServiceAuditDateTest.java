package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
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
public class ExpenseServiceAuditDateTest {

    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private PostTransactionRepository postTransactionRepository;

    @MockBean
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private NumberGenerationService numberGenerationService;

    @Test
    public void testCreateExpense_SetsAuditDate() {
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
        savedTransaction.setAuditDate(LocalDate.now());
        savedTransaction.setGuestName("Unknown Guest");

        when(numberGenerationService.generateTransactionId()).thenReturn("TXN001");
        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);
        when(hotelAccountHeadRepository.existsById("ACC001")).thenReturn(true);

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("TXN001", response.getTransactionId());
        assertEquals("V001", response.getVoucherNo());
        assertEquals("ACC001", response.getAccountHeadId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertNotNull(response.getAuditDate());
        assertEquals(LocalDate.now(), response.getAuditDate());
        assertEquals("Unknown Guest", response.getGuestName());
    }

    @Test
    public void testCreateExpense_WithRoomInfo() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setVoucherNo("V002");
        request.setDate(LocalDate.now());
        request.setAccountHeadId("ACC001");
        request.setAmount(new BigDecimal("150.00"));
        request.setNarration("Room service");
        request.setShiftNo("S001");
        request.setShiftDate(LocalDate.now());
        request.setRoomNo("101");

        PostTransaction savedTransaction = new PostTransaction();
        savedTransaction.setTransactionId("TXN002");
        savedTransaction.setVoucherNo("V002");
        savedTransaction.setDate(LocalDate.now());
        savedTransaction.setAccHeadId("ACC001");
        savedTransaction.setAmount(new BigDecimal("150.00"));
        savedTransaction.setNarration("Room service");
        savedTransaction.setShiftNo("S001");
        savedTransaction.setShiftDate(LocalDate.now());
        savedTransaction.setAuditDate(LocalDate.now());
        savedTransaction.setRoomId("ROOM001");
        savedTransaction.setGuestName("John Doe");

        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");

        when(numberGenerationService.generateTransactionId()).thenReturn("TXN002");
        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);
        when(hotelAccountHeadRepository.existsById("ACC001")).thenReturn(true);
        when(roomRepository.findByRoomNo("101")).thenReturn(java.util.Optional.of(room));
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room));

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("TXN002", response.getTransactionId());
        assertEquals("101", response.getRoomNo());
        assertEquals("ROOM001", response.getRoomId());
        assertEquals("John Doe", response.getGuestName());
        assertNotNull(response.getAuditDate());
    }
}