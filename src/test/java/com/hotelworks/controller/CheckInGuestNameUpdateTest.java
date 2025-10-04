package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.CheckInRequest;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.CheckInRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
public class CheckInGuestNameUpdateTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CheckInRepository checkInRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testUpdateCheckInWithGuestName() throws Exception {
        // Create a check-in entity
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOL20250921001");
        checkIn.setGuestName("John Doe");
        checkIn.setRoomId("RM101");
        checkIn.setArrivalDate(LocalDate.of(2025, 9, 21));
        checkIn.setDepartureDate(LocalDate.of(2025, 9, 23));
        checkIn.setMobileNumber("9876543210");
        checkIn.setWalkIn("N");

        // Mock repository behavior
        when(checkInRepository.findById(anyString())).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create request with updated guest name
        CheckInRequest request = new CheckInRequest();
        request.setGuestName("John Smith");
        request.setDepartureDate(LocalDate.of(2025, 9, 24));
        request.setRate(new BigDecimal("3000.00"));
        request.setRemarks("Extended stay requested");
        request.setMobileNumber("9876543210");
        request.setEmailId("john.doe@example.com");
        request.setWalkIn("N");

        // Perform the request
        mockMvc.perform(put("/api/checkins/FOL20250921001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Check-in updated successfully"))
                .andExpect(jsonPath("$.data.guestName").value("John Smith"))
                .andExpect(jsonPath("$.data.departureDate").value("2025-09-24"));
    }
}