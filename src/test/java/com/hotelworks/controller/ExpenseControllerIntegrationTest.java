package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.service.NumberGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateExpense_WithAllFields_ShouldSaveToPostTransaction() throws Exception {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setVoucherNo("V001");
        request.setDate(LocalDate.now());
        request.setAccountHeadId("ACC001");
        request.setAmount(new BigDecimal("100.00"));
        request.setNarration("Test expense");
        request.setShiftNo("S001");
        request.setShiftDate(LocalDate.now());
        request.setRoomNo("101");
        request.setFolioNo("F001");
        request.setGuestName("John Doe");

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
        when(checkInRepository.existsById("F001")).thenReturn(true);
        when(checkInRepository.findById("F001")).thenReturn(java.util.Optional.of(checkIn));

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense created successfully"))
                .andExpect(jsonPath("$.data.transactionId").value("TXN001"))
                .andExpect(jsonPath("$.data.roomNo").value("101"))
                .andExpect(jsonPath("$.data.roomId").value("ROOM001"))
                .andExpect(jsonPath("$.data.folioNo").value("F001"))
                .andExpect(jsonPath("$.data.guestName").value("John Doe"));

        // Verify that the save method was called
        verify(postTransactionRepository, times(1)).save(any(PostTransaction.class));
    }
}