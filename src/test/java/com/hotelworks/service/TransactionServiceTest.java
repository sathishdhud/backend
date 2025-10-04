package com.hotelworks.service;

import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private NumberGenerationService numberGenerationService;

    private ExpenseRequest validExpenseRequest;

    @BeforeEach
    public void setUp() {
        // Create a test account head
        HotelAccountHead accountHead = new HotelAccountHead();
        accountHead.setAccHeadId("ACC001");
        accountHead.setName("Test Account Head");
        hotelAccountHeadRepository.save(accountHead);

        // Create a test room
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");
        room.setFloor("1");
        room.setStatus("VR");
        roomRepository.save(room);

        // Create a valid expense request
        validExpenseRequest = new ExpenseRequest();
        validExpenseRequest.setVoucherNo("EXP-2023-001");
        validExpenseRequest.setDate(LocalDate.now());
        validExpenseRequest.setAccountHeadId("ACC001");
        validExpenseRequest.setAmount(new BigDecimal("1500.00"));
        validExpenseRequest.setNarration("Test expense");
        validExpenseRequest.setShiftNo("1");
        validExpenseRequest.setShiftDate(LocalDate.now());
        validExpenseRequest.setRoomNo("101");
    }

    @Test
    public void testCreateExpense_Success() {
        // When
        ExpenseResponse response = transactionService.createExpense(validExpenseRequest);

        // Then
        assertNotNull(response);
        assertEquals(validExpenseRequest.getVoucherNo(), response.getVoucherNo());
        assertEquals(validExpenseRequest.getDate(), response.getDate());
        assertEquals(validExpenseRequest.getAccountHeadId(), response.getAccountHeadId());
        assertEquals(validExpenseRequest.getAmount(), response.getAmount());
        assertEquals(validExpenseRequest.getNarration(), response.getNarration());
        assertEquals(validExpenseRequest.getShiftNo(), response.getShiftNo());
        assertEquals(validExpenseRequest.getShiftDate(), response.getShiftDate());
    }

    @Test
    public void testCreateExpense_InvalidAccountHead_ThrowsException() {
        // Given
        validExpenseRequest.setAccountHeadId("NONEXISTENT");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createExpense(validExpenseRequest);
        });

        assertTrue(exception.getMessage().contains("Account head not found"));
    }

    @Test
    public void testCreateExpense_InvalidRoom_ThrowsException() {
        // Given
        validExpenseRequest.setRoomNo("999"); // Non-existent room

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createExpense(validExpenseRequest);
        });

        assertTrue(exception.getMessage().contains("Room not found"));
    }
}