package com.hotelworks.service;

import com.hotelworks.dto.request.ReservationRequest;
import com.hotelworks.dto.response.ReservationResponse;
import com.hotelworks.entity.ResvSource;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.ResvSourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservationSourceMappingTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ResvSourceRepository resvSourceRepository;

    @Autowired
    private NumberGenerationService numberGenerationService;

    @Test
    public void testReservationSourceIsSavedAndRetrieved() {
        // First create a reservation source
        String resvSourceId = numberGenerationService.generateResvSourceId();
        ResvSource resvSource = new ResvSource();
        resvSource.setId(resvSourceId);
        resvSource.setResvSource("Test Booking Source");
        resvSourceRepository.save(resvSource);

        // Create a reservation with the reservation source
        ReservationRequest request = new ReservationRequest();
        request.setGuestName("Test Guest");
        request.setArrivalDate(LocalDate.now().plusDays(1));
        request.setDepartureDate(LocalDate.now().plusDays(3));
        request.setNoOfDays(2);
        request.setNoOfPersons(2);
        request.setNoOfRooms(1);
        request.setMobileNumber("1234567890");
        request.setRate(new BigDecimal("1000.00"));
        request.setIncludingGst("N");
        request.setResvSourceId(resvSourceId); // Set the reservation source ID

        // Create the reservation
        ReservationResponse response = reservationService.createReservation(request);

        // Verify that the reservation source ID and name are properly set
        assertNotNull(response.getResvSourceId(), "Reservation source ID should not be null");
        assertEquals(resvSourceId, response.getResvSourceId(), "Reservation source ID should match");
        assertNotNull(response.getResvSourceName(), "Reservation source name should not be null");
        assertEquals("Test Booking Source", response.getResvSourceName(), "Reservation source name should match");
    }
}