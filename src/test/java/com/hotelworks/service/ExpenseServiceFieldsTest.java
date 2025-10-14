package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.CheckInRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExpenseServiceFieldsTest {

    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private PostTransactionRepository postTransactionRepository;

    @MockBean
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private CheckInRepository checkInRepository;

    @MockBean
    private NumberGenerationService numberGenerationService;

    @Test
    public void testCreateExpense_WithRoomAndFolio_ShouldSetAllFields() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setVoucherNo("V001");
        request.setDate(LocalDate.of(2025, 10, 12));
        request.setAccountHeadId("ACC001");
        request.setAmount(new BigDecimal("100.00"));
        request.setNarration("Test expense");
        request.setShiftNo("S001");
        request.setShiftDate(LocalDate.of(2025, 10, 12));
        request.setRoomNo("101");
        request.setFolioNo("F001");
        request.setGuestName("John Doe");

        PostTransaction savedTransaction = new PostTransaction();
        savedTransaction.setTransactionId("TXN001");
        savedTransaction.setVoucherNo("V001");
        savedTransaction.setDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setAccHeadId("ACC001");
        savedTransaction.setAmount(new BigDecimal("100.00"));
        savedTransaction.setNarration("Test expense");
        savedTransaction.setShiftNo("S001");
        savedTransaction.setShiftDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setAuditDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setRoomId("ROOM001");
        savedTransaction.setFolioNo("F001");
        savedTransaction.setGuestName("John Doe");

        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");

        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("F001");
        checkIn.setGuestName("John Doe");

        when(numberGenerationService.generateTransactionId()).thenReturn("TXN001");
        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);
        when(hotelAccountHeadRepository.existsById("ACC001")).thenReturn(true);
        when(roomRepository.findByRoomNo("101")).thenReturn(java.util.Optional.of(room));
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room)); // This was missing
        when(checkInRepository.existsById("F001")).thenReturn(true);
        when(checkInRepository.findById("F001")).thenReturn(java.util.Optional.of(checkIn));

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("TXN001", response.getTransactionId());
        assertEquals("V001", response.getVoucherNo());
        assertEquals("ACC001", response.getAccountHeadId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("101", response.getRoomNo());
        assertEquals("ROOM001", response.getRoomId());
        assertEquals("F001", response.getFolioNo());
        assertEquals("John Doe", response.getGuestName());
        assertEquals(LocalDate.of(2025, 10, 12), response.getAuditDate());
    }

    @Test
    public void testCreateExpense_WithoutOptionalFields_ShouldSetDefaults() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setVoucherNo("V002");
        request.setDate(LocalDate.of(2025, 10, 12));
        request.setAccountHeadId("ACC002");
        request.setAmount(new BigDecimal("50.00"));
        request.setNarration("Simple expense");
        request.setShiftNo("S002");
        request.setShiftDate(LocalDate.of(2025, 10, 12));

        PostTransaction savedTransaction = new PostTransaction();
        savedTransaction.setTransactionId("TXN002");
        savedTransaction.setVoucherNo("V002");
        savedTransaction.setDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setAccHeadId("ACC002");
        savedTransaction.setAmount(new BigDecimal("50.00"));
        savedTransaction.setNarration("Simple expense");
        savedTransaction.setShiftNo("S002");
        savedTransaction.setShiftDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setAuditDate(LocalDate.of(2025, 10, 12));
        savedTransaction.setGuestName("Unknown Guest"); // Default value

        when(numberGenerationService.generateTransactionId()).thenReturn("TXN002");
        when(postTransactionRepository.save(any(PostTransaction.class))).thenReturn(savedTransaction);
        when(hotelAccountHeadRepository.existsById("ACC002")).thenReturn(true);

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals("TXN002", response.getTransactionId());
        assertEquals("V002", response.getVoucherNo());
        assertEquals("ACC002", response.getAccountHeadId());
        assertEquals(new BigDecimal("50.00"), response.getAmount());
        assertNull(response.getRoomNo());
        assertNull(response.getRoomId());
        assertNull(response.getFolioNo());
        assertNull(response.getBillNo());
        assertEquals("Unknown Guest", response.getGuestName());
        assertEquals(LocalDate.of(2025, 10, 12), response.getAuditDate());
    }
}