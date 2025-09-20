package com.hotelworks.service;

import com.hotelworks.dto.request.ReservationRequest;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.TaxationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ReservationGstHandlingTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TaxationRepository taxationRepository;

    @BeforeEach
    public void setUp() {
        // Clean up taxation repository
        taxationRepository.deleteAll();

        // Create test tax records
        Taxation cgstTax = new Taxation();
        cgstTax.setTaxId("CGST001");
        cgstTax.setTaxName("CGST");
        cgstTax.setPercentage(new BigDecimal("9.00"));
        taxationRepository.save(cgstTax);

        Taxation sgstTax = new Taxation();
        sgstTax.setTaxId("SGST001");
        sgstTax.setTaxName("SGST");
        sgstTax.setPercentage(new BigDecimal("9.00"));
        taxationRepository.save(sgstTax);
    }

    @Test
    public void testGstExclusiveRateHandling() {
        // Create reservation request with GST exclusive rate
        ReservationRequest request = new ReservationRequest();
        request.setGuestName("Test Guest");
        request.setArrivalDate(java.time.LocalDate.now().plusDays(1));
        request.setDepartureDate(java.time.LocalDate.now().plusDays(3));
        request.setNoOfDays(2);
        request.setNoOfPersons(2);
        request.setNoOfRooms(1);
        request.setMobileNumber("1234567890");
        request.setRate(new BigDecimal("1000.00"));
        request.setIncludingGst("N"); // GST exclusive

        // Create reservation
        var response = reservationService.createReservation(request);

        // Verify that the rate remains unchanged for GST exclusive
        assertNotNull(response);
        assertEquals(new BigDecimal("1000.00"), response.getRate());
        assertEquals("N", response.getIncludingGst());
    }

    @Test
    public void testGstInclusiveRateHandling() {
        // Create reservation request with GST inclusive rate
        ReservationRequest request = new ReservationRequest();
        request.setGuestName("Test Guest");
        request.setArrivalDate(java.time.LocalDate.now().plusDays(1));
        request.setDepartureDate(java.time.LocalDate.now().plusDays(3));
        request.setNoOfDays(2);
        request.setNoOfPersons(2);
        request.setNoOfRooms(1);
        request.setMobileNumber("1234567890");
        request.setRate(new BigDecimal("1000.00")); // Base rate
        request.setIncludingGst("Y"); // GST inclusive

        // Create reservation
        var response = reservationService.createReservation(request);

        // Verify that the rate is updated to include CGST and SGST
        assertNotNull(response);
        // Expected: 1000 * (1 + (9 + 9) / 100) = 1000 * 1.18 = 1180.00
        assertEquals(new BigDecimal("1180.00"), response.getRate());
        assertEquals("Y", response.getIncludingGst());
    }

    @Test
    public void testGstInclusiveRateUpdate() {
        // First create a reservation with GST exclusive
        ReservationRequest request1 = new ReservationRequest();
        request1.setGuestName("Test Guest");
        request1.setArrivalDate(java.time.LocalDate.now().plusDays(1));
        request1.setDepartureDate(java.time.LocalDate.now().plusDays(3));
        request1.setNoOfDays(2);
        request1.setNoOfPersons(2);
        request1.setNoOfRooms(1);
        request1.setMobileNumber("1234567890");
        request1.setRate(new BigDecimal("1000.00"));
        request1.setIncludingGst("N"); // GST exclusive

        var response1 = reservationService.createReservation(request1);

        // Then update it to GST inclusive
        ReservationRequest request2 = new ReservationRequest();
        request2.setGuestName("Test Guest Updated");
        request2.setArrivalDate(java.time.LocalDate.now().plusDays(1));
        request2.setDepartureDate(java.time.LocalDate.now().plusDays(3));
        request2.setNoOfDays(2);
        request2.setNoOfPersons(2);
        request2.setNoOfRooms(1);
        request2.setMobileNumber("1234567890");
        request2.setRate(new BigDecimal("1000.00"));
        request2.setIncludingGst("Y"); // GST inclusive

        var response2 = reservationService.updateReservation(response1.getReservationNo(), request2);

        // Verify that the rate is updated to include CGST and SGST
        assertNotNull(response2);
        // Expected: 1000 * (1 + (9 + 9) / 100) = 1000 * 1.18 = 1180.00
        assertEquals(new BigDecimal("1180.00"), response2.getRate());
        assertEquals("Y", response2.getIncludingGst());
    }
}