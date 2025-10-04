package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.ExpenseRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.ExpenseResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.repository.HotelAccountHeadRepository;
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
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    private ExpenseRequest validExpenseRequest;

    @BeforeEach
    public void setUp() {
        // Clean up repositories
        hotelAccountHeadRepository.deleteAll();

        // Create a test account head
        HotelAccountHead accountHead = new HotelAccountHead();
        accountHead.setAccHeadId("ACC001");
        accountHead.setName("Office Supplies");
        hotelAccountHeadRepository.save(accountHead);

        // Create another test account head for update
        HotelAccountHead accountHead2 = new HotelAccountHead();
        accountHead2.setAccHeadId("ACC002");
        accountHead2.setName("Maintenance");
        hotelAccountHeadRepository.save(accountHead2);

        // Create a valid expense request
        validExpenseRequest = new ExpenseRequest();
        validExpenseRequest.setVoucherNo("EXP-2023-001");
        validExpenseRequest.setDate(LocalDate.now());
        validExpenseRequest.setAccountHeadId("ACC001");
        validExpenseRequest.setAmount(new BigDecimal("1500.00"));
        validExpenseRequest.setNarration("Office supplies purchase");
        validExpenseRequest.setShiftNo("1");
        validExpenseRequest.setShiftDate(LocalDate.now());
    }

    @Test
    public void testCreateExpense_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense created successfully"))
                .andExpect(jsonPath("$.data.transactionId").exists())
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-001"))
                .andExpect(jsonPath("$.data.accountHeadId").value("ACC001"));
    }

    @Test
    public void testGetAllExpenses_Success() throws Exception {
        // First create an expense
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)));

        // Then get all expenses
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].transactionId").exists());
    }

    @Test
    public void testGetExpenseById_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the expenseId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String expenseId = apiResponse.getData().getTransactionId();

        // Then get the expense by ID
        mockMvc.perform(get("/api/expenses/{expenseId}", expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").value(expenseId))
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-001"));
    }

    @Test
    public void testUpdateExpense_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the expenseId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String expenseId = apiResponse.getData().getTransactionId();

        // Create an updated expense request
        ExpenseRequest updatedRequest = new ExpenseRequest();
        updatedRequest.setVoucherNo("EXP-2023-002");
        updatedRequest.setDate(LocalDate.now());
        updatedRequest.setAccountHeadId("ACC002");
        updatedRequest.setAmount(new BigDecimal("2500.00"));
        updatedRequest.setNarration("Maintenance services");
        updatedRequest.setShiftNo("2");
        updatedRequest.setShiftDate(LocalDate.now());

        // Then update the expense
        mockMvc.perform(put("/api/expenses/{expenseId}", expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense updated successfully"))
                .andExpect(jsonPath("$.data.transactionId").value(expenseId))
                .andExpect(jsonPath("$.data.voucherNo").value("EXP-2023-002"))
                .andExpect(jsonPath("$.data.accountHeadId").value("ACC002"));
    }

    @Test
    public void testDeleteExpense_Success() throws Exception {
        // First create an expense
        String response = mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)))
                .andReturn().getResponse().getContentAsString();

        // Parse the response to get the expenseId
        ApiResponse<ExpenseResponse> apiResponse = objectMapper.readValue(
            response, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ExpenseResponse.class)
        );
        String expenseId = apiResponse.getData().getTransactionId();

        // Then delete the expense
        mockMvc.perform(delete("/api/expenses/{expenseId}", expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Expense deleted successfully"));

        // Verify the expense is deleted by trying to get it
        mockMvc.perform(get("/api/expenses/{expenseId}", expenseId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve expense")));
    }

    @Test
    public void testGetExpensesByVoucherNo_Success() throws Exception {
        // First create an expense
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)));

        // Then get expenses by voucher number
        mockMvc.perform(get("/api/expenses/voucher/{voucherNo}", "EXP-2023-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].voucherNo").value("EXP-2023-001"));
    }

    @Test
    public void testGetExpensesByAccountHead_Success() throws Exception {
        // First create an expense
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExpenseRequest)));

        // Then get expenses by account head
        mockMvc.perform(get("/api/expenses/account-head/{accountHeadId}", "ACC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].accountHeadId").value("ACC001"));
    }
}