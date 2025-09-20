package com.hotelworks.scheduler;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Reservation;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class GstHandlingTest {

    @Autowired
    private RoomStatusScheduler roomStatusScheduler;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    public void setUp() {
        // Clean up repositories
        checkInRepository.deleteAll();
        reservationRepository.deleteAll();
        roomRepository.deleteAll();

        // Create test room
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");
        room.setFloor("1");
        room.setStatus("OD");
        roomRepository.save(room);
    }

    @Test
    public void testGstExclusiveRateHandling() {
        // Create a reservation with GST exclusive rate
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES001");
        reservation.setGuestName("Test Guest");
        reservation.setRate(new BigDecimal("1000.00"));
        reservation.setIncludingGst("N"); // GST exclusive
        reservation.setArrivalDate(LocalDate.now().minusDays(2));
        reservation.setDepartureDate(LocalDate.now().plusDays(3));
        reservationRepository.save(reservation);

        // Create check-in record
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO001");
        checkIn.setReservationNo("RES001");
        checkIn.setGuestName("Test Guest");
        checkIn.setRoomId("ROOM001");
        checkIn.setArrivalDate(LocalDate.now().minusDays(2));
        checkIn.setDepartureDate(LocalDate.now().plusDays(3));
        checkIn.setRate(new BigDecimal("1000.00"));
        checkIn.setMobileNumber("1234567890");
        checkInRepository.save(checkIn);

        // Verify that the rate is properly set
        CheckIn savedCheckIn = checkInRepository.findById("FOLIO001").orElse(null);
        assertNotNull(savedCheckIn);
        assertEquals(new BigDecimal("1000.00"), savedCheckIn.getRate());
        assertFalse("Y".equalsIgnoreCase(reservation.getIncludingGst()));
    }

    @Test
    public void testGstInclusiveRateHandling() {
        // Create a reservation with GST inclusive rate
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES002");
        reservation.setGuestName("Test Guest");
        reservation.setRate(new BigDecimal("1180.00")); // 1000 + 18% GST
        reservation.setIncludingGst("Y"); // GST inclusive
        reservation.setArrivalDate(LocalDate.now().minusDays(2));
        reservation.setDepartureDate(LocalDate.now().plusDays(3));
        reservationRepository.save(reservation);

        // Create check-in record
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO002");
        checkIn.setReservationNo("RES002");
        checkIn.setGuestName("Test Guest");
        checkIn.setRoomId("ROOM001");
        checkIn.setArrivalDate(LocalDate.now().minusDays(2));
        checkIn.setDepartureDate(LocalDate.now().plusDays(3));
        checkIn.setRate(new BigDecimal("1180.00"));
        checkIn.setMobileNumber("1234567890");
        checkInRepository.save(checkIn);

        // Verify that the rate is properly set
        CheckIn savedCheckIn = checkInRepository.findById("FOLIO002").orElse(null);
        assertNotNull(savedCheckIn);
        assertEquals(new BigDecimal("1180.00"), savedCheckIn.getRate());
        assertTrue("Y".equalsIgnoreCase(reservation.getIncludingGst()));
    }
}