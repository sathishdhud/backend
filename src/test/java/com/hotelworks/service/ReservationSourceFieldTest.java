package com.hotelworks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationSourceFieldTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testReservationRequestWithResvSourceId() throws Exception {
        String json = "{\n" +
                "  \"guestName\": \"Test Guest\",\n" +
                "  \"arrivalDate\": \"2025-10-14\",\n" +
                "  \"departureDate\": \"2025-10-17\",\n" +
                "  \"noOfDays\": 3,\n" +
                "  \"noOfPersons\": 1,\n" +
                "  \"noOfRooms\": 1,\n" +
                "  \"mobileNumber\": \"1234567890\",\n" +
                "  \"resvSourceId\": \"SRC9185\"\n" +
                "}";

        ReservationRequest request = objectMapper.readValue(json, ReservationRequest.class);
        assertEquals("SRC9185", request.getResvSourceId());
    }

    @Test
    public void testReservationRequestWithReservationSourceId() throws Exception {
        String json = "{\n" +
                "  \"guestName\": \"Test Guest\",\n" +
                "  \"arrivalDate\": \"2025-10-14\",\n" +
                "  \"departureDate\": \"2025-10-17\",\n" +
                "  \"noOfDays\": 3,\n" +
                "  \"noOfPersons\": 1,\n" +
                "  \"noOfRooms\": 1,\n" +
                "  \"mobileNumber\": \"1234567890\",\n" +
                "  \"reservationSourceId\": \"SRC9185\"\n" +
                "}";

        ReservationRequest request = objectMapper.readValue(json, ReservationRequest.class);
        assertEquals("SRC9185", request.getResvSourceId());
    }
}