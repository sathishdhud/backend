package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Autowired
    private RoomRepository roomRepository;

    private ExpenseRequest validExpenseRequest;

    @BeforeEach
    public void setUp() {
        // Clean up repositories
        hotelAccountHeadRepository.deleteAll();
        roomRepository.deleteAll();

        // Create a test account head
        HotelAccountHead accountHead = new HotelAccountHead();
        accountHead.setAccHeadId("ACC001");
        accountHead.setName("Test Account Head");
        hotelAccountHeadRepository.save(accountHead);

        // Create another test account head for update
        HotelAccountHead accountHead2 = new HotelAccountHead();
        accountHead2.setAccHeadId("ACC002");
        accountHead2.setName("Updated Account Head");
        hotelAccountHeadRepository.save(accountHead2);

        // Create a test room
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");
        room.setFloor("1");
        room.setStatus("VR");
        roomRepository.save(room);

        // Create another test room for update
        Room room2 = new Room();
        room2.setRoomId("ROOM002");
        room2.setRoomNo("102");
        room2.setFloor("1");
        room2.setStatus("VR");
        roomRepository.save(room2);

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
    public void testCreateExpense_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/transactions/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").exists())
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-001"))
                .andExpect(jsonPath("$.data.accountHeadId").value("ACC001"));
    }

    @Test
    public void testGetAllExpenses_Success() throws Exception {
        // First create an expense
        mockMvc.perform(post("/api/transactions/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)));

        // Then get all expenses
        mockMvc.perform(get("/api/transactions/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].transactionId").exists());
    }

    @Test
    public void testGetExpenseByTransactionId_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/transactions/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the transactionId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String transactionId = apiResponse.getData().getTransactionId();

        // Then get the expense by transactionId
        mockMvc.perform(get("/api/transactions/expenses/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").value(transactionId))
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-001"));
    }

    @Test
    public void testUpdateExpense_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/transactions/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the transactionId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String transactionId = apiResponse.getData().getTransactionId();

        // Create an updated expense request
        ExpenseRequest updatedRequest = new ExpenseRequest();
        updatedRequest.setVoucherNo("EXP-2023-002");
        updatedRequest.setDate(LocalDate.now());
        updatedRequest.setAccountHeadId("ACC002");
        updatedRequest.setAmount(new BigDecimal("2500.00"));
        updatedRequest.setNarration("Updated expense");
        updatedRequest.setShiftNo("2");
        updatedRequest.setShiftDate(LocalDate.now());
        updatedRequest.setRoomNo("102");

        // Then update the expense
        mockMvc.perform(put("/api/transactions/expenses/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense updated successfully"))
                .andExpect(jsonPath("$.data.transactionId").value(transactionId))
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-002"))
                .andExpect(jsonPath("$.data.accountHeadId").value("ACC002"));
    }

    @Test
    public void testDeleteExpense_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/transactions/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the transactionId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String transactionId = apiResponse.getData().getTransactionId();

        // Then delete the expense
        mockMvc.perform(delete("/api/transactions/expenses/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense deleted successfully"));

        // Verify the expense is deleted by trying to get it
        mockMvc.perform(get("/api/transactions/expenses/{transactionId}", transactionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve expense")));
    }
}