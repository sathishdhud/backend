package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.AdvanceRequest;
import com.hotelworks.dto.response.AdvanceResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.AdvanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdvanceController.class)
public class AdvanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvanceService advanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAdvanceForReservationByPath() throws Exception {
        // Setup test data
        String reservationNo = "RES001";
        
        AdvanceRequest request = new AdvanceRequest();
        request.setGuestName("Test Guest");
        request.setModeOfPaymentId("CASH");
        request.setAmount(new BigDecimal("500.00"));
        
        AdvanceResponse response = new AdvanceResponse();
        response.setReceiptNo("ADV001");
        response.setReservationNo(reservationNo);
        response.setGuestName("Test Guest");
        response.setAmount(new BigDecimal("500.00"));
        response.setModeOfPaymentId("CASH");
        
        // Mock service response
        when(advanceService.createAdvanceForReservation(any(AdvanceRequest.class)))
            .thenReturn(response);
        
        // Perform request and verify response
        mockMvc.perform(post("/api/advances/reservation/{reservationNo}", reservationNo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Advance created successfully for reservation"))
                .andExpect(jsonPath("$.data.receiptNo").value("ADV001"))
                .andExpect(jsonPath("$.data.reservationNo").value(reservationNo));
    }
}